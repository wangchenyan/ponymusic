package me.wcy.music.service

import android.content.Context
import android.media.AudioManager
import android.media.AudioManager.OnAudioFocusChangeListener

/**
 * Created by hzwangchenyan on 2017/8/8.
 */
class AudioFocusManager(context: Context) : OnAudioFocusChangeListener {
    private val audioManager: AudioManager
    private var isPausedByFocusLossTransient = false

    init {
        audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }

    fun requestAudioFocus(): Boolean {
        return (audioManager.requestAudioFocus(
            this,
            AudioManager.STREAM_MUSIC,
            AudioManager.AUDIOFOCUS_GAIN
        )
                == AudioManager.AUDIOFOCUS_REQUEST_GRANTED)
    }

    fun abandonAudioFocus() {
        audioManager.abandonAudioFocus(this)
    }

    override fun onAudioFocusChange(focusChange: Int) {
        when (focusChange) {
            AudioManager.AUDIOFOCUS_GAIN -> {
                if (isPausedByFocusLossTransient) {
                    // 通话结束，恢复播放
                    AudioPlayer.get().startPlayer()
                }

                // 恢复音量
                AudioPlayer.get().mediaPlayer.setVolume(1f, 1f)
                isPausedByFocusLossTransient = false
            }

            AudioManager.AUDIOFOCUS_LOSS -> AudioPlayer.get().pausePlayer()
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                AudioPlayer.get().pausePlayer(false)
                isPausedByFocusLossTransient = true
            }

            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK ->                 // 音量减小为一半
                AudioPlayer.get().mediaPlayer.setVolume(0.5f, 0.5f)
        }
    }
}