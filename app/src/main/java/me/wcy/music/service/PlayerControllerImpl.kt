package me.wcy.music.service

import androidx.annotation.MainThread
import androidx.annotation.OptIn
import androidx.lifecycle.MutableLiveData
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.wcy.music.storage.db.MusicDatabase
import me.wcy.music.storage.preference.ConfigPreferences
import me.wcy.music.utils.toMediaItem
import me.wcy.music.utils.toSongEntity
import top.wangchenyan.common.ext.toUnMutable
import top.wangchenyan.common.ext.toast

/**
 * Created by wangchenyan.top on 2024/3/27.
 */
class PlayerControllerImpl(
    private val player: Player,
    private val db: MusicDatabase,
) : PlayerController, CoroutineScope by MainScope() {

    private val _playlist = MutableLiveData(emptyList<MediaItem>())
    override val playlist = _playlist.toUnMutable()

    private val _currentSong = MutableLiveData<MediaItem?>(null)
    override val currentSong = _currentSong.toUnMutable()

    private val _playState = MutableStateFlow<PlayState>(PlayState.Idle)
    override val playState = _playState.toUnMutable()

    private val _playProgress = MutableStateFlow<Long>(0)
    override val playProgress = _playProgress.toUnMutable()

    private val _bufferingPercent = MutableStateFlow(0)
    override val bufferingPercent = _bufferingPercent.toUnMutable()

    private val _playMode = MutableStateFlow(PlayMode.valueOf(ConfigPreferences.playMode))
    override val playMode: StateFlow<PlayMode> = _playMode

    private var audioSessionId = 0

    init {
        player.playWhenReady = false
        player.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                super.onPlaybackStateChanged(playbackState)
                when (playbackState) {
                    Player.STATE_IDLE -> {
                        _playState.value = PlayState.Idle
                        _playProgress.value = 0
                        _bufferingPercent.value = 0
                    }

                    Player.STATE_BUFFERING -> {
                        _playState.value = PlayState.Preparing
                    }

                    Player.STATE_READY -> {
                        player.play()
                        _playState.value = PlayState.Playing
                    }

                    Player.STATE_ENDED -> {
                    }
                }
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                super.onIsPlayingChanged(isPlaying)
                if (player.playbackState == Player.STATE_READY) {
                    _playState.value = if (isPlaying) PlayState.Playing else PlayState.Pause
                }
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                super.onMediaItemTransition(mediaItem, reason)
                mediaItem ?: return
                val playlist = _playlist.value ?: return
                _currentSong.value = playlist.find { it.mediaId == mediaItem.mediaId }
            }

            @OptIn(UnstableApi::class)
            override fun onAudioSessionIdChanged(audioSessionId: Int) {
                super.onAudioSessionIdChanged(audioSessionId)
                this@PlayerControllerImpl.audioSessionId = audioSessionId
            }

            override fun onPlayerError(error: PlaybackException) {
                super.onPlayerError(error)
                stop()
                toast("播放失败(${error.errorCodeName},${error.localizedMessage})")
            }
        })
        setPlayMode(PlayMode.valueOf(ConfigPreferences.playMode))

        launch(Dispatchers.Main.immediate) {
            val playlist = withContext(Dispatchers.IO) {
                db.playlistDao()
                    .queryAll()
                    .onEach {
                        // 兼容老版本数据库
                        if (it.uri.isEmpty()) {
                            it.uri = it.path
                        }
                    }
                    .map { it.toMediaItem() }
            }
            if (playlist.isNotEmpty()) {
                _playlist.value = playlist
                player.setMediaItems(playlist)
                val currentSongId = ConfigPreferences.currentSongId
                if (currentSongId.isNotEmpty()) {
                    val currentSongIndex = playlist.indexOfFirst {
                        it.mediaId == currentSongId
                    }.coerceAtLeast(0)
                    _currentSong.value = playlist[currentSongIndex]
                    player.seekTo(currentSongIndex, 0)
                }
            }

            _currentSong.observeForever {
                ConfigPreferences.currentSongId = it?.mediaId ?: ""
            }
        }

        launch {
            while (true) {
                if (player.isPlaying) {
                    _playProgress.value = player.currentPosition
                }
                delay(1000)
            }
        }
    }

    @MainThread
    override fun addAndPlay(song: MediaItem) {
        launch(Dispatchers.Main.immediate) {
            val newPlaylist = _playlist.value?.toMutableList() ?: mutableListOf()
            val index = newPlaylist.indexOfFirst { it.mediaId == song.mediaId }
            if (index >= 0) {
                newPlaylist[index] = song
                player.replaceMediaItem(index, song)
            } else {
                newPlaylist.add(song)
                player.addMediaItem(song)
            }
            withContext(Dispatchers.IO) {
                db.playlistDao().clear()
                db.playlistDao().insertAll(newPlaylist.map { it.toSongEntity() })
            }
            _playlist.value = newPlaylist
            play(song.mediaId)
        }
    }

    @MainThread
    override fun replaceAll(songList: List<MediaItem>, song: MediaItem) {
        launch(Dispatchers.Main.immediate) {
            withContext(Dispatchers.IO) {
                db.playlistDao().clear()
                db.playlistDao().insertAll(songList.map { it.toSongEntity() })
            }
            stop()
            player.setMediaItems(songList)
            _playlist.value = songList
            _currentSong.value = song
            play(song.mediaId)
        }
    }

    @MainThread
    override fun play(mediaId: String) {
        val playlist = _playlist.value
        if (playlist.isNullOrEmpty()) {
            return
        }
        val index = playlist.indexOfFirst { it.mediaId == mediaId }
        if (index < 0) {
            return
        }

        stop()
        player.seekTo(index, 0)
        player.prepare()

        _currentSong.value = playlist[index]
        _playProgress.value = 0
        _bufferingPercent.value = 0
    }

    @MainThread
    override fun delete(song: MediaItem) {
        launch(Dispatchers.Main.immediate) {
            val playlist = _playlist.value?.toMutableList() ?: mutableListOf()
            val index = playlist.indexOfFirst { it.mediaId == song.mediaId }
            if (index < 0) return@launch
            if (playlist.size == 1) {
                clearPlaylist()
            } else {
                playlist.removeAt(index)
                _playlist.value = playlist
                withContext(Dispatchers.IO) {
                    db.playlistDao().delete(song.toSongEntity())
                }
                player.removeMediaItem(index)
            }
        }
    }

    @MainThread
    override fun clearPlaylist() {
        launch(Dispatchers.Main.immediate) {
            withContext(Dispatchers.IO) {
                db.playlistDao().clear()
            }
            stop()
            player.clearMediaItems()
            _playlist.value = emptyList()
            _currentSong.value = null
        }
    }

    @MainThread
    override fun playPause() {
        if (player.mediaItemCount == 0) return
        when (player.playbackState) {
            Player.STATE_IDLE -> {
                player.prepare()
            }

            Player.STATE_BUFFERING -> {
                stop()
            }

            Player.STATE_READY -> {
                if (player.isPlaying) {
                    player.pause()
                    _playState.value = PlayState.Pause
                } else {
                    player.play()
                    _playState.value = PlayState.Playing
                }
            }

            Player.STATE_ENDED -> {
                player.seekToNextMediaItem()
                player.prepare()
            }
        }
    }

    @MainThread
    override fun next() {
        if (player.mediaItemCount == 0) return
        player.seekToNextMediaItem()
        player.prepare()
        _playProgress.value = 0
        _bufferingPercent.value = 0
    }

    @MainThread
    override fun prev() {
        if (player.mediaItemCount == 0) return
        player.seekToPreviousMediaItem()
        player.prepare()
        _playProgress.value = 0
        _bufferingPercent.value = 0
    }

    @MainThread
    override fun seekTo(msec: Int) {
        if (player.playbackState == Player.STATE_READY) {
            player.seekTo(msec.toLong())
        }
    }

    @MainThread
    override fun getAudioSessionId(): Int {
        return audioSessionId
    }

    @MainThread
    override fun setPlayMode(mode: PlayMode) {
        ConfigPreferences.playMode = mode.value
        _playMode.value = mode
        when (mode) {
            PlayMode.Loop -> {
                player.repeatMode = Player.REPEAT_MODE_ALL
                player.shuffleModeEnabled = false
            }

            PlayMode.Shuffle -> {
                player.repeatMode = Player.REPEAT_MODE_ALL
                player.shuffleModeEnabled = true
            }

            PlayMode.Single -> {
                player.repeatMode = Player.REPEAT_MODE_ONE
                player.shuffleModeEnabled = false
            }
        }
    }

    @MainThread
    override fun stop() {
        player.stop()
        _playState.value = PlayState.Idle
    }
}