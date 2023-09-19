package me.wcy.music.service.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.support.v4.media.session.MediaSessionCompat
import android.view.KeyEvent
import dagger.hilt.android.AndroidEntryPoint
import me.wcy.music.service.AudioPlayer
import javax.inject.Inject

/**
 * 耳机线控，仅在5.0以下有效，5.0以上被[MediaSessionCompat]接管。
 * Created by hzwangchenyan on 2016/1/21.
 */
@AndroidEntryPoint
class RemoteControlReceiver : BroadcastReceiver() {
    @Inject
    lateinit var audioPlayer: AudioPlayer

    override fun onReceive(context: Context, intent: Intent) {
        val event = intent.getParcelableExtra<KeyEvent>(Intent.EXTRA_KEY_EVENT)
        if (event == null || event.action != KeyEvent.ACTION_UP) {
            return
        }
        when (event.keyCode) {
            KeyEvent.KEYCODE_MEDIA_PLAY,
            KeyEvent.KEYCODE_MEDIA_PAUSE,
            KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE,
            KeyEvent.KEYCODE_HEADSETHOOK -> {
                audioPlayer.playPause()
            }

            KeyEvent.KEYCODE_MEDIA_NEXT -> {
                audioPlayer.next()
            }

            KeyEvent.KEYCODE_MEDIA_PREVIOUS -> {
                audioPlayer.prev()
            }
        }
    }
}