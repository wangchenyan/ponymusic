package me.wcy.music.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import me.wcy.music.application.Notifier
import me.wcy.music.constants.Actions

/**
 * 音乐播放后台服务
 * Created by wcy on 2015/11/27.
 */
class PlayService : Service() {
    inner class PlayBinder : Binder() {
        val service: PlayService
            get() = this@PlayService
    }

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "onCreate: " + javaClass.simpleName)
        AudioPlayer.get()
        MediaSessionManager.get().init(this)
        Notifier.get().init(this)
        QuitTimer.get().init()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return PlayBinder()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action != null) {
            when (intent.action) {
                Actions.ACTION_STOP -> stop()
            }
        }
        return START_NOT_STICKY
    }

    private fun stop() {
        AudioPlayer.get().stopPlayer()
        QuitTimer.get().stop()
        Notifier.get().destroy()
    }

    companion object {
        private const val TAG = "Service"
        fun startCommand(context: Context, action: String) {
            val intent = Intent(context, PlayService::class.java)
            intent.action = action
            context.startService(intent)
        }
    }
}