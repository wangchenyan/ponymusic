package me.wcy.music.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.support.v4.media.session.MediaSessionCompat
import android.view.KeyEvent
import me.wcy.music.service.AudioPlayer

/**
 * 耳机线控，仅在5.0以下有效，5.0以上被[MediaSessionCompat]接管。
 * Created by hzwangchenyan on 2016/1/21.
 */
class RemoteControlReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val event = intent.getParcelableExtra<KeyEvent>(Intent.EXTRA_KEY_EVENT)
        if (event == null || event.action != KeyEvent.ACTION_UP) {
            return
        }
        when (event.keyCode) {
            KeyEvent.KEYCODE_MEDIA_PLAY, KeyEvent.KEYCODE_MEDIA_PAUSE, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE, KeyEvent.KEYCODE_HEADSETHOOK -> AudioPlayer.get()
                .playPause()

            KeyEvent.KEYCODE_MEDIA_NEXT -> AudioPlayer.get().next()
            KeyEvent.KEYCODE_MEDIA_PREVIOUS -> AudioPlayer.get().prev()
        }
    }
}