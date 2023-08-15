package me.wcy.music.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import me.wcy.music.service.AudioPlayer

/**
 * Created by wcy on 2017/4/18.
 */
class StatusBarReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent == null || TextUtils.isEmpty(intent.action)) {
            return
        }
        val extra = intent.getStringExtra(EXTRA)
        if (TextUtils.equals(extra, EXTRA_NEXT)) {
            AudioPlayer.get().next()
        } else if (TextUtils.equals(extra, EXTRA_PLAY_PAUSE)) {
            AudioPlayer.get().playPause()
        }
    }

    companion object {
        const val ACTION_STATUS_BAR = "me.wcy.music.STATUS_BAR_ACTIONS"
        const val EXTRA = "extra"
        const val EXTRA_NEXT = "next"
        const val EXTRA_PLAY_PAUSE = "play_pause"
    }
}