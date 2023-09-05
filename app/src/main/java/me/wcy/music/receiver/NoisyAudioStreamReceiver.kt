package me.wcy.music.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import me.wcy.music.service.AudioPlayer2
import javax.inject.Inject

/**
 * 来电/耳机拔出时暂停播放
 * Created by wcy on 2016/1/23.
 */
@AndroidEntryPoint
class NoisyAudioStreamReceiver : BroadcastReceiver() {
    @Inject
    lateinit var audioPlayer: AudioPlayer2

    override fun onReceive(context: Context, intent: Intent) {
        audioPlayer.playPause()
    }
}