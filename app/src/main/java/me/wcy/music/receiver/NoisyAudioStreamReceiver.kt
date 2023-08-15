package me.wcy.music.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import me.wcy.music.service.AudioPlayer

/**
 * 来电/耳机拔出时暂停播放
 * Created by wcy on 2016/1/23.
 */
class NoisyAudioStreamReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        AudioPlayer.get().playPause()
    }
}