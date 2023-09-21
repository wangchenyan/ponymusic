package me.wcy.music.service

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.os.Binder
import android.os.IBinder
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.blankj.utilcode.util.IntentUtils
import com.blankj.utilcode.util.StringUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import me.wcy.common.CommonApp
import me.wcy.common.permission.Permissioner
import me.wcy.common.utils.image.ImageUtils
import me.wcy.music.R
import me.wcy.music.ext.registerReceiverCompat
import me.wcy.music.service.receiver.StatusBarReceiver
import me.wcy.music.storage.db.entity.SongEntity
import me.wcy.music.utils.MusicUtils

/**
 * 音乐播放后台服务
 * Created by wcy on 2015/11/27.
 */
@AndroidEntryPoint
class PlayService : Service() {
    private val notificationReceiver by lazy {
        StatusBarReceiver()
    }
    private var loadCoverJob: Job? = null
    private var isChannelCreated = false

    inner class PlayBinder : Binder() {
        val service: PlayService
            get() = this@PlayService
    }

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "onCreate: " + javaClass.simpleName)
        createNotificationChannel()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return PlayBinder()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent ?: return START_NOT_STICKY
        if (intent.action != null) {
            when (intent.action) {
                ACTION_SHOW_NOTIFICATION -> {
                    val isPlaying = intent.getBooleanExtra("is_playing", false)
                    val music = intent.getParcelableExtra<SongEntity>("music")
                    if (music != null) {
                        showNotification(isPlaying, music)
                    }
                }

                ACTION_CANCEL_NOTIFICATION -> {
                    cancelNotification()
                }
            }
        }
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        cancelNotification()
        unregisterNotificationReceiver()
    }

    private fun showNotification(isPlaying: Boolean, song: SongEntity) {
        if (checkNotificationPermission()) {
            loadCoverJob?.cancel()
            if (isPlaying) {
                startForeground(
                    NOTIFICATION_ID,
                    buildNotification(song, null, true)
                )
                loadCoverJob = CommonApp.appScope.launch {
                    val bitmap = ImageUtils.loadBitmap(song.albumCover).data
                    if (bitmap != null) {
                        startForeground(
                            NOTIFICATION_ID,
                            buildNotification(song, bitmap, true)
                        )
                    }
                }
            } else {
                stopForeground(false)
                NotificationManagerCompat.from(this)
                    .notify(
                        NOTIFICATION_ID,
                        buildNotification(song, null, false)
                    )
                loadCoverJob = CommonApp.appScope.launch {
                    val bitmap = ImageUtils.loadBitmap(song.albumCover).data
                    if (bitmap != null) {
                        NotificationManagerCompat.from(this@PlayService)
                            .notify(
                                NOTIFICATION_ID,
                                buildNotification(song, bitmap, false)
                            )
                    }
                }
            }
        }
    }

    private fun cancelNotification() {
        NotificationManagerCompat.from(this).cancel(NOTIFICATION_ID)
    }

    private fun checkNotificationPermission(): Boolean {
        if (Permissioner.hasNotificationPermission(this)) {
            createNotificationChannel()
            return true
        }
        return false
    }

    private fun createNotificationChannel() {
        if (isChannelCreated.not() && Permissioner.hasNotificationPermission(this)) {
            isChannelCreated = true
            val name = StringUtils.getString(R.string.app_name)
            val descriptionText = "音乐通知栏"
            val importance = NotificationManagerCompat.IMPORTANCE_LOW
            val mChannel =
                NotificationChannelCompat.Builder(NOTIFICATION_ID.toString(), importance)
                    .setName(name)
                    .setDescription(descriptionText)
                    .setVibrationEnabled(false)
                    .build()
            NotificationManagerCompat.from(this)
                .createNotificationChannel(mChannel)
            registerNotificationReceiver()
        }
    }

    private fun registerNotificationReceiver() {
        registerReceiverCompat(
            notificationReceiver,
            IntentFilter(StatusBarReceiver.ACTION_STATUS_BAR),
        )
    }

    private fun unregisterNotificationReceiver() {
        unregisterReceiver(notificationReceiver)
    }

    private fun buildNotification(
        song: SongEntity,
        cover: Bitmap?,
        isPlaying: Boolean
    ): Notification {
        val intent = IntentUtils.getLaunchAppIntent(packageName)
        intent.putExtra(EXTRA_NOTIFICATION, true)
        intent.action = Intent.ACTION_VIEW
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val pendingIntent =
            PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            )
        val builder = NotificationCompat.Builder(this, NOTIFICATION_ID.toString())
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.ic_notification)
            .setCustomContentView(getRemoteViews(song, cover, isPlaying))
        return builder.build()
    }

    private fun getRemoteViews(
        song: SongEntity,
        cover: Bitmap?,
        isPlaying: Boolean
    ): RemoteViews {
        val title = song.title
        val subtitle = MusicUtils.getArtistAndAlbum(song.artist, song.album)
        val remoteViews = RemoteViews(packageName, R.layout.notification)
        if (cover != null) {
            remoteViews.setImageViewBitmap(R.id.iv_icon, cover)
        } else {
            remoteViews.setImageViewResource(R.id.iv_icon, R.drawable.ic_default_cover)
        }
        remoteViews.setTextViewText(R.id.tv_title, title)
        remoteViews.setTextViewText(R.id.tv_subtitle, subtitle)
        val playIntent = Intent(StatusBarReceiver.ACTION_STATUS_BAR)
        playIntent.putExtra(
            StatusBarReceiver.EXTRA,
            StatusBarReceiver.EXTRA_PLAY_PAUSE
        )
        val playPendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            playIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
        val playIconRes = if (isPlaying) {
            R.drawable.ic_notification_pause
        } else {
            R.drawable.ic_notification_play
        }
        remoteViews.setImageViewResource(R.id.iv_play_pause, playIconRes)
        remoteViews.setOnClickPendingIntent(R.id.iv_play_pause, playPendingIntent)
        val nextIntent = Intent(StatusBarReceiver.ACTION_STATUS_BAR)
        nextIntent.putExtra(StatusBarReceiver.EXTRA, StatusBarReceiver.EXTRA_NEXT)
        val nextPendingIntent = PendingIntent.getBroadcast(
            this,
            1,
            nextIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
        remoteViews.setImageViewResource(R.id.iv_next, R.drawable.ic_notification_next)
        remoteViews.setOnClickPendingIntent(R.id.iv_next, nextPendingIntent)
        return remoteViews
    }

    companion object {
        const val EXTRA_NOTIFICATION = "me.wcy.music.notification"

        private const val TAG = "Service"
        private const val NOTIFICATION_ID = 0x111
        private const val ACTION_SHOW_NOTIFICATION = "me.wcy.music.ACTION_SHOW_NOTIFICATION"
        private const val ACTION_CANCEL_NOTIFICATION = "me.wcy.music.ACTION_CANCEL_NOTIFICATION"

        fun showNotification(context: Context, isPlaying: Boolean, music: SongEntity) {
            val intent = Intent(context, PlayService::class.java)
            intent.action = ACTION_SHOW_NOTIFICATION
            intent.putExtra("is_playing", isPlaying)
            intent.putExtra("music", music)
            context.startService(intent)
        }

        fun cancelNotification(context: Context) {
            val intent = Intent(context, PlayService::class.java)
            intent.action = ACTION_CANCEL_NOTIFICATION
            context.startService(intent)
        }
    }
}