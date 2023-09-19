package me.wcy.music.service

import android.content.Context
import android.media.AudioManager
import android.media.AudioManager.OnAudioFocusChangeListener

/**
 * Created by hzwangchenyan on 2017/8/8.
 */
class AudioFocusManager(
    private val context: Context,
    private val audioPlayer: AudioPlayer
) {
    private val audioManager by lazy {
        context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }
    private var isPausedByFocusLossTransient = false

    fun requestAudioFocus(): Boolean {
        return (audioManager.requestAudioFocus(
            focusChangeListener,
            AudioManager.STREAM_MUSIC,
            AudioManager.AUDIOFOCUS_GAIN
        ) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED)
    }

    fun abandonAudioFocus() {
        audioManager.abandonAudioFocus(focusChangeListener)
    }

    private val focusChangeListener =
        OnAudioFocusChangeListener { focusChange ->
            when (focusChange) {
                AudioManager.AUDIOFOCUS_GAIN -> {
                    if (isPausedByFocusLossTransient) {
                        // 通话结束，恢复播放
                        audioPlayer.startPlayer()
                    }

                    // 恢复音量
                    audioPlayer.setVolume(1f, 1f)
                    isPausedByFocusLossTransient = false
                }

                AudioManager.AUDIOFOCUS_LOSS -> {
                    audioPlayer.pausePlayer()
                }

                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                    audioPlayer.pausePlayer(false)
                    isPausedByFocusLossTransient = true
                }

                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                    // 音量减小为一半
                    audioPlayer.setVolume(0.5f, 0.5f)
                }
            }
        }
}