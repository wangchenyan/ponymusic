package me.wcy.music.application

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.blankj.utilcode.util.StringUtils
import me.wcy.common.CommonApp
import me.wcy.common.permission.Permissioner
import me.wcy.music.R
import me.wcy.music.activity.MusicActivity
import me.wcy.music.const.Extras
import me.wcy.music.model.Music
import me.wcy.music.receiver.StatusBarReceiver
import me.wcy.music.service.PlayService
import me.wcy.music.utils.CoverLoader
import me.wcy.music.utils.FileUtils

/**
 * Created by wcy on 2017/4/18.
 */
class Notifier private constructor() {
    private var playService: PlayService? = null
    private var notificationReceiver: StatusBarReceiver? = null
    private var isChannelCreated = false

    private object SingletonHolder {
        val instance = Notifier()
    }

    fun init(service: PlayService) {
        playService = service
        createNotificationChannel()
    }

    fun showPlay(music: Music?) {
        music ?: return
        if (checkNotificationPermission()) {
            playService?.startForeground(
                NOTIFICATION_ID,
                buildNotification(CommonApp.app, music, true)
            )
        }
    }

    fun showPause(music: Music?) {
        music ?: return
        if (checkNotificationPermission()) {
            playService?.stopForeground(false)
            NotificationManagerCompat.from(CommonApp.app)
                .notify(
                    NOTIFICATION_ID,
                    buildNotification(CommonApp.app, music, false)
                )
        }
    }

    fun destroy() {
        unregisterReceiver()
        NotificationManagerCompat.from(CommonApp.app)
            .cancelAll()
        playService = null
        isChannelCreated = false
    }

    private fun checkNotificationPermission(): Boolean {
        if (Permissioner.hasNotificationPermission(CommonApp.app)) {
            createNotificationChannel()
            return true
        }
        return false
    }

    private fun createNotificationChannel() {
        if (isChannelCreated.not() && Permissioner.hasNotificationPermission(CommonApp.app)) {
            isChannelCreated = true
            val name = StringUtils.getString(R.string.app_name)
            val descriptionText = "音乐通知栏"
            val importance = NotificationManagerCompat.IMPORTANCE_LOW
            val mChannel = NotificationChannelCompat.Builder(NOTIFICATION_ID.toString(), importance)
                .setName(name)
                .setDescription(descriptionText)
                .setVibrationEnabled(false)
                .build()
            NotificationManagerCompat.from(CommonApp.app)
                .createNotificationChannel(mChannel)
            registerReceiver()
        }
    }

    private fun registerReceiver() {
        if (notificationReceiver == null) {
            notificationReceiver = StatusBarReceiver()
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            CommonApp.app.registerReceiver(
                notificationReceiver,
                IntentFilter(StatusBarReceiver.ACTION_STATUS_BAR),
                Context.RECEIVER_EXPORTED
            )
        } else {
            CommonApp.app.registerReceiver(
                notificationReceiver,
                IntentFilter(StatusBarReceiver.ACTION_STATUS_BAR),
            )
        }
    }

    private fun unregisterReceiver() {
        if (notificationReceiver != null) {
            CommonApp.app.unregisterReceiver(notificationReceiver)
            notificationReceiver = null
        }
    }

    private fun buildNotification(
        context: Context,
        music: Music,
        isPlaying: Boolean
    ): Notification {
        val intent = Intent(context, MusicActivity::class.java)
        intent.putExtra(Extras.EXTRA_NOTIFICATION, true)
        intent.action = Intent.ACTION_VIEW
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val pendingIntent =
            PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            )
        val builder = NotificationCompat.Builder(context, NOTIFICATION_ID.toString())
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.ic_notification)
            .setCustomContentView(getRemoteViews(context, music, isPlaying))
        return builder.build()
    }

    private fun getRemoteViews(context: Context, music: Music, isPlaying: Boolean): RemoteViews {
        val title = music.title
        val subtitle = FileUtils.getArtistAndAlbum(music.artist, music.album)
        val cover: Bitmap? = CoverLoader.get().loadThumb(music)
        val remoteViews = RemoteViews(context.packageName, R.layout.notification)
        if (cover != null) {
            remoteViews.setImageViewBitmap(R.id.iv_icon, cover)
        } else {
            remoteViews.setImageViewResource(R.id.iv_icon, R.drawable.ic_launcher)
        }
        remoteViews.setTextViewText(R.id.tv_title, title)
        remoteViews.setTextViewText(R.id.tv_subtitle, subtitle)
        val playIntent = Intent(StatusBarReceiver.ACTION_STATUS_BAR)
        playIntent.putExtra(
            StatusBarReceiver.EXTRA,
            StatusBarReceiver.EXTRA_PLAY_PAUSE
        )
        val playPendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            playIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
        val playIconRes = if (isPlaying) {
            R.drawable.ic_status_bar_pause
        } else {
            R.drawable.ic_status_bar_play
        }
        remoteViews.setImageViewResource(R.id.iv_play_pause, playIconRes)
        remoteViews.setOnClickPendingIntent(R.id.iv_play_pause, playPendingIntent)
        val nextIntent = Intent(StatusBarReceiver.ACTION_STATUS_BAR)
        nextIntent.putExtra(StatusBarReceiver.EXTRA, StatusBarReceiver.EXTRA_NEXT)
        val nextPendingIntent = PendingIntent.getBroadcast(
            context,
            1,
            nextIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
        remoteViews.setImageViewResource(R.id.iv_next, R.drawable.ic_status_bar_next)
        remoteViews.setOnClickPendingIntent(R.id.iv_next, nextPendingIntent)
        return remoteViews
    }

    companion object {
        private const val NOTIFICATION_ID = 0x111
        fun get(): Notifier {
            return SingletonHolder.instance
        }
    }
}