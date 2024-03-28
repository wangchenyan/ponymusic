package me.wcy.music.service

import android.app.PendingIntent
import android.content.Intent
import androidx.annotation.OptIn
import androidx.media3.common.AudioAttributes
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.session.DefaultMediaNotificationProvider
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.blankj.utilcode.util.IntentUtils
import me.wcy.music.R
import me.wcy.music.net.datasource.MusicDataSource
import top.wangchenyan.common.CommonApp

/**
 * Created by wangchenyan.top on 2024/3/26.
 */
class MusicService : MediaSessionService() {
    private lateinit var player: Player
    private lateinit var session: MediaSession

    @OptIn(UnstableApi::class)
    override fun onCreate() {
        super.onCreate()

        @OptIn(UnstableApi::class)
        player = ExoPlayer.Builder(applicationContext)
            // 自动处理音频焦点
            .setAudioAttributes(AudioAttributes.DEFAULT, true)
            // 自动暂停播放
            .setHandleAudioBecomingNoisy(true)
            .setMediaSourceFactory(
                DefaultMediaSourceFactory(applicationContext)
                    .setDataSourceFactory(MusicDataSource.Factory(applicationContext))
            )
            .build()

        session = MediaSession.Builder(this, player)
            .setSessionActivity(
                PendingIntent.getActivity(
                    this,
                    0,
                    IntentUtils.getLaunchAppIntent(packageName).apply {
                        putExtra(EXTRA_NOTIFICATION, true)
                        action = Intent.ACTION_VIEW
                        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    },
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            )
            .build()

        setMediaNotificationProvider(
            DefaultMediaNotificationProvider.Builder(applicationContext).build().apply {
                setSmallIcon(R.drawable.ic_notification)
            }
        )
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return session
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        player.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
        session.release()
    }

    companion object {
        val EXTRA_NOTIFICATION = "${CommonApp.app.packageName}.notification"
    }
}