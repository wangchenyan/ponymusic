package me.wcy.music.service

import android.content.IntentFilter
import android.media.AudioManager
import android.media.MediaPlayer
import android.util.Log
import androidx.annotation.MainThread
import androidx.lifecycle.MutableLiveData
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
import me.wcy.common.net.apiCall
import me.wcy.music.discover.DiscoverApi
import me.wcy.music.ext.registerReceiverCompat
import me.wcy.music.service.receiver.NoisyAudioStreamReceiver
import me.wcy.music.storage.db.MusicDatabase
import me.wcy.music.storage.db.entity.SongEntity
import me.wcy.music.storage.preference.MusicPreferences
import java.util.Random
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by hzwangchenyan on 2018/1/26.
 */
@Singleton
class AudioPlayerImpl @Inject constructor(
    private val db: MusicDatabase,
) : AudioPlayer, CoroutineScope by MainScope() {
    private val context by lazy {
        CommonApp.app
    }
    private val mediaPlayer by lazy {
        MediaPlayer()
    }
    private val mediaSessionManager by lazy {
        MediaSessionManager(context, this)
    }
    private val audioFocusManager by lazy {
        AudioFocusManager(context, this)
    }
    private val noisyReceiver by lazy {
        NoisyAudioStreamReceiver()
    }
    private val noisyFilter: IntentFilter by lazy {
        IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
    }

    private val _playlist = MutableLiveData(emptyList<SongEntity>())
    override val playlist = _playlist.toUnMutable()

    private val _currentSong = MutableLiveData<SongEntity?>(null)
    override val currentSong = _currentSong.toUnMutable()

    private val _playState = MutableStateFlow<PlayState>(PlayState.Idle)
    override val playState = _playState.toUnMutable()

    private val _playProgress = MutableStateFlow<Long>(0)
    override val playProgress = _playProgress.toUnMutable()

    private val _bufferingPercent = MutableStateFlow(0)
    override val bufferingPercent = _bufferingPercent.toUnMutable()

    private var updateProgressJob: Job? = null
    private var getSongUrlJob: Job? = null

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
        mediaPlayer.setOnErrorListener { mp, what, extra ->
            onPlayError()
            true
        }

        launch(Dispatchers.Main.immediate) {
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

    @MainThread
    override fun addAndPlay(song: SongEntity) {
        launch(Dispatchers.Main.immediate) {
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

    @MainThread
    override fun replaceAll(songList: List<SongEntity>, song: SongEntity) {
        launch(Dispatchers.Main.immediate) {
            withContext(Dispatchers.IO) {
                db.playlistDao().clear()
                db.playlistDao().insertAll(songList)
            }
            _playlist.value = songList
            _currentSong.value = song
            play(song)
        }
    }

    @MainThread
    override fun play(song: SongEntity?) {
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

        getSongUrlJob?.cancel()
        getSongUrlJob = null

        _currentSong.value = playSong
        _playProgress.value = 0
        _bufferingPercent.value = 0
        _playState.value = PlayState.Preparing
        PlayService.showNotification(context, true, playSong)
        mediaSessionManager.updateMetaData(playSong)
        mediaSessionManager.updatePlaybackState()
        mediaPlayer.reset()

        val realPlay = {
            kotlin.runCatching {
                mediaPlayer.setDataSource(playSong.path)
                mediaPlayer.prepareAsync()
            }.onFailure {
                Log.e(TAG, "play error", it)
                onPlayError()
            }
        }

        if (playSong.isLocal()) {
            realPlay()
        } else {
            getSongUrlJob = launch(Dispatchers.Main.immediate) {
                val res = apiCall { DiscoverApi.get().getSongUrl(playSong.songId) }
                if (res.isSuccessWithData() && res.getDataOrThrow().isNotEmpty()) {
                    playSong.path = res.getDataOrThrow().first().url
                    realPlay()
                } else {
                    onPlayError()
                }
            }
        }
    }

    @MainThread
    override fun delete(song: SongEntity) {
        launch(Dispatchers.Main.immediate) {
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

    @MainThread
    override fun playPause() {
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

    @MainThread
    override fun startPlayer() {
        if (_playState.value.isPreparing.not()
            && _playState.value.isPausing.not()
        ) {
            return
        }
        if (audioFocusManager.requestAudioFocus()) {
            mediaPlayer.start()
            _playState.value = PlayState.Playing

            updateProgressJob = launch(Dispatchers.Main.immediate) {
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

    @MainThread
    override fun pausePlayer(abandonAudioFocus: Boolean) {
        if (_playState.value.isPlaying.not()) {
            return
        }
        mediaPlayer.pause()
        _playState.value = PlayState.Pause
        updateProgressJob?.cancel()
        updateProgressJob = null
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

    @MainThread
    override fun stopPlayer() {
        if (_playState.value.isIdle) {
            return
        }
        getSongUrlJob?.cancel()
        getSongUrlJob = null
        pausePlayer()
        mediaPlayer.reset()
        _playState.value = PlayState.Idle
    }

    @MainThread
    override fun next() {
        val playlist = _playlist.value
        if (playlist.isNullOrEmpty()) {
            return
        }
        val mode = PlayMode.valueOf(MusicPreferences.playMode)
        when (mode) {
            PlayMode.Shuffle -> {
                play(playlist[Random().nextInt(playlist.size)])
            }

            PlayMode.Single -> {
                play(_currentSong.value)
            }

            PlayMode.Loop -> {
                var position = playlist.indexOf(_currentSong.value) + 1
                if (position >= playlist.size) {
                    position = 0
                }
                play(playlist[position])
            }
        }
    }

    @MainThread
    override fun prev() {
        val playlist = _playlist.value
        if (playlist.isNullOrEmpty()) {
            return
        }
        val mode = PlayMode.valueOf(MusicPreferences.playMode)
        when (mode) {
            PlayMode.Shuffle -> {
                play(playlist[Random().nextInt(playlist.size)])
            }

            PlayMode.Single -> {
                play(_currentSong.value)
            }

            PlayMode.Loop -> {
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
    @MainThread
    override fun seekTo(msec: Int) {
        if (_playState.value.isPlaying || _playState.value.isPausing) {
            mediaPlayer.seekTo(msec)
            mediaSessionManager.updatePlaybackState()
            _playProgress.value = msec.toLong()
        }
    }

    @MainThread
    override fun setVolume(leftVolume: Float, rightVolume: Float) {
        mediaPlayer.setVolume(leftVolume, rightVolume)
    }

    @MainThread
    override fun getAudioPosition(): Long {
        return if (_playState.value.isPlaying || _playState.value.isPausing) {
            mediaPlayer.currentPosition.toLong()
        } else {
            0
        }
    }

    @MainThread
    override fun getAudioSessionId() = mediaPlayer.audioSessionId

    private fun onPlayError() {
        stopPlayer()
        toast("播放失败")
    }

    companion object {
        private const val TAG = "AudioPlayer"
        private const val TIME_UPDATE = 300L
    }
}