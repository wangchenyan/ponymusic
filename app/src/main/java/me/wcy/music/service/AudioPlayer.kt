package me.wcy.music.service

import android.content.IntentFilter
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import me.wcy.common.CommonApp
import me.wcy.music.application.Notifier
import me.wcy.music.enums.PlayModeEnum
import me.wcy.music.model.Music
import me.wcy.music.receiver.NoisyAudioStreamReceiver
import me.wcy.music.storage.preference.Preferences
import me.wcy.music.utils.ToastUtils
import java.io.IOException
import java.util.Random

/**
 * Created by hzwangchenyan on 2018/1/26.
 */
class AudioPlayer private constructor() {
    val mediaPlayer: MediaPlayer = MediaPlayer()
    private val audioFocusManager: AudioFocusManager by lazy {
        AudioFocusManager(CommonApp.app)
    }
    private val handler: Handler by lazy {
        Handler(Looper.getMainLooper())
    }
    private val noisyReceiver: NoisyAudioStreamReceiver by lazy {
        NoisyAudioStreamReceiver()
    }
    private val noisyFilter: IntentFilter by lazy {
        IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
    }
    private val musicList: MutableList<Music> = mutableListOf()
    private val listeners: MutableList<OnPlayerEventListener> = ArrayList()
    private var state = STATE_IDLE

    private object SingletonHolder {
        val instance = AudioPlayer()
    }

    init {
        // TODO
        // DBManager.get().musicDao.queryBuilder().build().list().filterNotNull().toMutableList()
        mediaPlayer.setOnCompletionListener { mp: MediaPlayer? -> next() }
        mediaPlayer.setOnPreparedListener { mp: MediaPlayer? ->
            if (isPreparing) {
                startPlayer()
            }
        }
        mediaPlayer.setOnBufferingUpdateListener { mp: MediaPlayer?, percent: Int ->
            for (listener in listeners) {
                listener.onBufferingUpdate(percent)
            }
        }
    }

    fun addOnPlayEventListener(listener: OnPlayerEventListener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener)
        }
    }

    fun removeOnPlayEventListener(listener: OnPlayerEventListener?) {
        listeners.remove(listener)
    }

    fun addAndPlay(music: Music) {
        var position = musicList.indexOf(music)
        if (position < 0) {
            musicList.add(music)
            // TODO
            // DBManager.get().musicDao.insert(music)
            position = musicList.size - 1
        }
        play(position)
    }

    fun play(position: Int) {
        var pos = position
        if (musicList.isEmpty()) {
            return
        }
        if (pos < 0) {
            pos = musicList.size - 1
        } else if (pos >= musicList.size) {
            pos = 0
        }
        playPosition = pos
        val music = playMusic!!
        try {
            mediaPlayer.reset()
            mediaPlayer.setDataSource(music.path)
            mediaPlayer.prepareAsync()
            state = STATE_PREPARING
            for (listener in listeners) {
                listener!!.onChange(music)
            }
            Notifier.get().showPlay(music)
            MediaSessionManager.get().updateMetaData(music)
            MediaSessionManager.get().updatePlaybackState()
        } catch (e: IOException) {
            e.printStackTrace()
            ToastUtils.show("当前歌曲无法播放")
        }
    }

    fun delete(position: Int) {
        val playPosition = playPosition
        val music = musicList.removeAt(position)
        // TODO
        // DBManager.get().musicDao.delete(music)
        if (playPosition > position) {
            this.playPosition = playPosition - 1
        } else if (playPosition == position) {
            if (isPlaying || isPreparing) {
                this.playPosition = playPosition - 1
                next()
            } else {
                stopPlayer()
                for (listener in listeners) {
                    listener!!.onChange(playMusic)
                }
            }
        }
    }

    fun playPause() {
        if (isPreparing) {
            stopPlayer()
        } else if (isPlaying) {
            pausePlayer()
        } else if (isPausing) {
            startPlayer()
        } else {
            play(playPosition)
        }
    }

    fun startPlayer() {
        if (!isPreparing && !isPausing) {
            return
        }
        if (audioFocusManager!!.requestAudioFocus()) {
            mediaPlayer!!.start()
            state = STATE_PLAYING
            handler!!.post(mPublishRunnable)
            Notifier.get().showPlay(playMusic)
            MediaSessionManager.get().updatePlaybackState()
            CommonApp.app.registerReceiver(noisyReceiver, noisyFilter)
            for (listener in listeners) {
                listener.onPlayerStart()
            }
        }
    }

    @JvmOverloads
    fun pausePlayer(abandonAudioFocus: Boolean = true) {
        if (!isPlaying) {
            return
        }
        mediaPlayer!!.pause()
        state = STATE_PAUSE
        handler!!.removeCallbacks(mPublishRunnable)
        Notifier.get().showPause(playMusic)
        MediaSessionManager.get().updatePlaybackState()
        CommonApp.app.unregisterReceiver(noisyReceiver)
        if (abandonAudioFocus) {
            audioFocusManager!!.abandonAudioFocus()
        }
        for (listener in listeners) {
            listener!!.onPlayerPause()
        }
    }

    fun stopPlayer() {
        if (isIdle) {
            return
        }
        pausePlayer()
        mediaPlayer!!.reset()
        state = STATE_IDLE
    }

    operator fun next() {
        if (musicList!!.isEmpty()) {
            return
        }
        val mode: PlayModeEnum = PlayModeEnum.valueOf(Preferences.playMode)
        when (mode) {
            PlayModeEnum.SHUFFLE -> play(Random().nextInt(musicList!!.size))
            PlayModeEnum.SINGLE -> play(playPosition)
            PlayModeEnum.LOOP -> play(playPosition + 1)
            else -> play(playPosition + 1)
        }
    }

    fun prev() {
        if (musicList!!.isEmpty()) {
            return
        }
        val mode: PlayModeEnum = PlayModeEnum.valueOf(Preferences.playMode)
        when (mode) {
            PlayModeEnum.SHUFFLE -> play(Random().nextInt(musicList!!.size))
            PlayModeEnum.SINGLE -> play(playPosition)
            PlayModeEnum.LOOP -> play(playPosition - 1)
            else -> play(playPosition - 1)
        }
    }

    /**
     * 跳转到指定的时间位置
     *
     * @param msec 时间
     */
    fun seekTo(msec: Int) {
        if (isPlaying || isPausing) {
            mediaPlayer!!.seekTo(msec)
            MediaSessionManager.get().updatePlaybackState()
            for (listener in listeners) {
                listener!!.onPublish(msec)
            }
        }
    }

    private val mPublishRunnable: Runnable = object : Runnable {
        override fun run() {
            if (isPlaying) {
                for (listener in listeners) {
                    listener!!.onPublish(mediaPlayer!!.currentPosition)
                }
            }
            handler!!.postDelayed(this, TIME_UPDATE)
        }
    }
    val audioSessionId: Int
        get() = mediaPlayer!!.audioSessionId
    val audioPosition: Long
        get() = if (isPlaying || isPausing) {
            mediaPlayer!!.currentPosition.toLong()
        } else {
            0
        }
    val playMusic: Music?
        get() = if (musicList!!.isEmpty()) {
            null
        } else musicList!![playPosition]

    fun getMusicList(): List<Music>? {
        return musicList
    }

    val isPlaying: Boolean
        get() = state == STATE_PLAYING
    val isPausing: Boolean
        get() = state == STATE_PAUSE
    val isPreparing: Boolean
        get() = state == STATE_PREPARING
    val isIdle: Boolean
        get() = state == STATE_IDLE
    var playPosition: Int
        get() {
            var position = Preferences.playPosition
            if (position < 0 || position >= musicList!!.size) {
                position = 0
                Preferences.savePlayPosition(position)
            }
            return position
        }
        private set(position) {
            Preferences.savePlayPosition(position)
        }

    companion object {
        private const val STATE_IDLE = 0
        private const val STATE_PREPARING = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSE = 3
        private const val TIME_UPDATE = 300L
        fun get(): AudioPlayer {
            return SingletonHolder.instance
        }
    }
}