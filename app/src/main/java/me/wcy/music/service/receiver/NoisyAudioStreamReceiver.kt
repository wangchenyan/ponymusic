package me.wcy.music.service.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import me.wcy.music.service.AudioPlayer
import javax.inject.Inject

/**
 * 来电/耳机拔出时暂停播放
 * Created by wcy on 2016/1/23.
 */
@AndroidEntryPoint
class NoisyAudioStreamReceiver : BroadcastReceiver() {
    @Inject
    lateinit var audioPlayer: AudioPlayer

    override fun onReceive(context: Context, intent: Intent) {
        audioPlayer.pausePlayer()
    }
}