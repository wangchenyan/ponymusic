package me.wcy.music.service

import android.app.Application
import android.content.IntentFilter
import android.media.AudioManager
import android.media.MediaPlayer
import androidx.lifecycle.MutableLiveData
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.wcy.common.CommonApp
import me.wcy.common.ext.toUnMutable
import me.wcy.common.ext.toast
import me.wcy.music.enums.PlayModeEnum
import me.wcy.music.ext.accessEntryPoint
import me.wcy.music.ext.registerReceiverCompat
import me.wcy.music.receiver.NoisyAudioStreamReceiver
import me.wcy.music.storage.db.MusicDatabase
import me.wcy.music.storage.db.entity.SongEntity
import me.wcy.music.storage.preference.MusicPreferences
import java.io.IOException
import java.util.Random
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by hzwangchenyan on 2018/1/26.
 */
@Singleton
class AudioPlayer2 @Inject constructor(
    private val db: MusicDatabase,
) : CoroutineScope by MainScope() {
    private val context by lazy {
        CommonApp.app
    }
    private val mediaPlayer by lazy {
        MediaPlayer()
    }
    private val mediaSessionManager by lazy {
        MediaSessionManager2(context, this)
    }
    private val audioFocusManager by lazy {
        AudioFocusManager2(context, this)
    }
    private val noisyReceiver by lazy {
        NoisyAudioStreamReceiver()
    }
    private val noisyFilter: IntentFilter by lazy {
        IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
    }

    private val _playlist = MutableLiveData(emptyList<SongEntity>())
    val playlist = _playlist.toUnMutable()

    private val _currentSong = MutableLiveData<SongEntity?>(null)
    val currentSong = _currentSong.toUnMutable()

    private val _playState = MutableStateFlow<PlayState>(PlayState.Idle)
    val playState = _playState.toUnMutable()

    private val _playProgress = MutableStateFlow<Long>(0)
    val playProgress = _playProgress.toUnMutable()

    private val _bufferingPercent = MutableStateFlow(0)
    val bufferingPercent = _bufferingPercent.toUnMutable()

    private var updateProgressJob: Job? = null

    init {
        mediaPlayer.setOnCompletionListener { mp: MediaPlayer? -> next() }
        mediaPlayer.setOnPreparedListener { mp: MediaPlayer? ->
            if (_playState.value.isPreparing) {
                startPlayer()
            }
        }
        mediaPlayer.setOnBufferingUpdateListener { mp: MediaPlayer?, percent: Int ->
            _bufferingPercent.value = percent
        }

        launch {
            withContext(Dispatchers.IO) {
                val playlist = db.playlistDao().queryAll()
                _playlist.postValue(playlist)
                val currentSongId = MusicPreferences.currentSongId
                if (currentSongId.isNotEmpty()) {
                    val song =
                        db.playlistDao().queryByUniqueId(currentSongId) ?: playlist.firstOrNull()
                    _currentSong.postValue(song)
                }
            }

            _currentSong.observeForever {
                MusicPreferences.currentSongId = it?.uniqueId ?: ""
            }
        }
    }

    fun addAndPlay(song: SongEntity) {
        launch {
            val newPlaylist = _playlist.value!!.toMutableList()
            val index = newPlaylist.indexOf(song)
            if (index >= 0) {
                newPlaylist[index] = song
            } else {
                newPlaylist.add(song)
            }
            withContext(Dispatchers.IO) {
                db.playlistDao().clear()
                db.playlistDao().insertAll(newPlaylist)
            }
            _playlist.value = newPlaylist
            play(song)
        }
    }

    fun replaceAll(songList: List<SongEntity>, song: SongEntity) {
        launch {
            withContext(Dispatchers.IO) {
                db.playlistDao().clear()
                db.playlistDao().insertAll(songList)
            }
            _playlist.value = songList
            _currentSong.value = song
            play(song)
        }
    }

    fun play(song: SongEntity?) {
        val playlist = _playlist.value!!
        if (playlist.isEmpty()) {
            return
        }
        if (song != null && song !in playlist) {
            return
        }
        var playSong = song
        if (playSong == null) {
            playSong = playlist.first()
        }
        try {
            mediaPlayer.reset()
            mediaPlayer.setDataSource(playSong.path)
            mediaPlayer.prepareAsync()
            _playState.value = PlayState.Preparing
            _currentSong.value = playSong
            PlayService.showNotification(context, true, playSong)
            mediaSessionManager.updateMetaData(playSong)
            mediaSessionManager.updatePlaybackState()
        } catch (e: IOException) {
            e.printStackTrace()
            toast("当前歌曲无法播放")
        }
    }

    fun delete(song: SongEntity) {
        launch {
            val playlist = _playlist.value!!.toMutableList()
            val index = playlist.indexOf(song)
            if (index < 0) return@launch
            playlist.removeAt(index)
            _playlist.value = playlist
            withContext(Dispatchers.IO) {
                db.playlistDao().delete(song)
            }
            if (song == _currentSong.value) {
                _currentSong.value = playlist.getOrNull((index - 1).coerceAtLeast(0))
                if ((_playState.value.isPlaying || _playState.value.isPreparing)
                    && playlist.isNotEmpty()
                ) {
                    next()
                } else {
                    stopPlayer()
                }
            }
        }
    }

    fun playPause() {
        when (_playState.value) {
            PlayState.Preparing -> {
                stopPlayer()
            }

            PlayState.Playing -> {
                pausePlayer()
            }

            PlayState.Pause -> {
                startPlayer()
            }

            else -> {
                play(_currentSong.value)
            }
        }
    }

    fun startPlayer() {
        if (_playState.value.isPreparing.not()
            && _playState.value.isPausing.not()
        ) {
            return
        }
        if (audioFocusManager.requestAudioFocus()) {
            mediaPlayer.start()
            _playState.value = PlayState.Playing

            updateProgressJob = launch {
                while (true) {
                    if (_playState.value.isPlaying) {
                        _playProgress.value = mediaPlayer.currentPosition.toLong()
                    }
                    delay(TIME_UPDATE)
                }
            }
            PlayService.showNotification(context, true, _currentSong.value!!)
            mediaSessionManager.updatePlaybackState()
            context.registerReceiverCompat(noisyReceiver, noisyFilter)
        }
    }

    fun pausePlayer(abandonAudioFocus: Boolean = true) {
        if (_playState.value.isPlaying.not()) {
            return
        }
        mediaPlayer.pause()
        _playState.value = PlayState.Pause
        updateProgressJob?.cancel()
        _currentSong.value?.also {
            PlayService.showNotification(context, false, it)
        } ?: {
            PlayService.cancelNotification(context)
        }
        mediaSessionManager.updatePlaybackState()
        context.unregisterReceiver(noisyReceiver)
        if (abandonAudioFocus) {
            audioFocusManager.abandonAudioFocus()
        }
    }

    fun stopPlayer() {
        if (_playState.value.isIdle) {
            return
        }
        pausePlayer()
        mediaPlayer.reset()
        _playState.value = PlayState.Idle
    }

    fun next() {
        val playlist = _playlist.value
        if (playlist.isNullOrEmpty()) {
            return
        }
        val mode = PlayModeEnum.valueOf(MusicPreferences.playMode)
        when (mode) {
            PlayModeEnum.SHUFFLE -> {
                play(playlist[Random().nextInt(playlist.size)])
            }

            PlayModeEnum.SINGLE -> {
                play(_currentSong.value)
            }

            PlayModeEnum.LOOP -> {
                var position = playlist.indexOf(_currentSong.value) + 1
                if (position >= playlist.size) {
                    position = 0
                }
                play(playlist[position])
            }
        }
    }

    fun prev() {
        val playlist = _playlist.value
        if (playlist.isNullOrEmpty()) {
            return
        }
        val mode = PlayModeEnum.valueOf(MusicPreferences.playMode)
        when (mode) {
            PlayModeEnum.SHUFFLE -> {
                play(playlist[Random().nextInt(playlist.size)])
            }

            PlayModeEnum.SINGLE -> {
                play(_currentSong.value)
            }

            PlayModeEnum.LOOP -> {
                var position = playlist.indexOf(_currentSong.value) - 1
                if (position < 0) {
                    position = playlist.size - 1
                }
                play(playlist[position])
            }
        }
    }

    /**
     * 跳转到指定的时间位置
     *
     * @param msec 时间
     */
    fun seekTo(msec: Int) {
        if (_playState.value.isPlaying || _playState.value.isPausing) {
            mediaPlayer.seekTo(msec)
            mediaSessionManager.updatePlaybackState()
            _playProgress.value = msec.toLong()
        }
    }

    fun setVolume(leftVolume: Float, rightVolume: Float) {
        mediaPlayer.setVolume(leftVolume, rightVolume)
    }

    fun getAudioPosition(): Long {
        return if (_playState.value.isPlaying || _playState.value.isPausing) {
            mediaPlayer.currentPosition.toLong()
        } else {
            0
        }
    }

    fun getAudioSessionId() = mediaPlayer.audioSessionId

    companion object {
        private const val TIME_UPDATE = 300L

        fun Application.audioPlayer(): AudioPlayer2 {
            return accessEntryPoint<AudioPlayerEntryPoint>().audioPlayer()
        }
    }

    sealed class PlayState {
        object Idle : PlayState()
        object Preparing : PlayState()
        object Playing : PlayState()
        object Pause : PlayState()

        val isIdle: Boolean
            get() = this is Idle
        val isPreparing: Boolean
            get() = this is Preparing
        val isPlaying: Boolean
            get() = this is Playing
        val isPausing: Boolean
            get() = this is Pause
    }

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface AudioPlayerEntryPoint {
        fun audioPlayer(): AudioPlayer2
    }
}