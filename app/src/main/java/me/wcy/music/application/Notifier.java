package me.wcy.music.application;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import me.wcy.music.R;
import me.wcy.music.activity.SplashActivity;
import me.wcy.music.constants.Extras;
import me.wcy.music.model.Music;
import me.wcy.music.receiver.StatusBarReceiver;
import me.wcy.music.service.PlayService;
import me.wcy.music.utils.CoverLoader;
import me.wcy.music.utils.FileUtils;

/**
 * Created by wcy on 2017/4/18.
 */
public class Notifier {
    private static final int NOTIFICATION_ID = 0x111;
    private static PlayService playService;
    private static NotificationManager notificationManager;

    public static void init(PlayService playService) {
        Notifier.playService = playService;
        notificationManager = (NotificationManager) playService.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public static void updateNotification(Music music) {
        notificationManager.cancel(NOTIFICATION_ID);
        playService.startForeground(NOTIFICATION_ID, buildNotification(playService, music));
    }

    public static void cancelNotification(Music music) {
        playService.stopForeground(true);
        notificationManager.notify(NOTIFICATION_ID, buildNotification(playService, music));
    }

    public static void cancelAll() {
        notificationManager.cancelAll();
    }

    private static Notification buildNotification(Context context, Music music) {
        Intent intent = new Intent(context, SplashActivity.class);
        intent.putExtra(Extras.EXTRA_NOTIFICATION, true);
        intent.setAction(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_notification)
                .setCustomContentView(getRemoteViews(context, music));
        return builder.build();
    }

    private static RemoteViews getRemoteViews(Context context, Music music) {
        String title = music.getTitle();
        String subtitle = FileUtils.getArtistAndAlbum(music.getArtist(), music.getAlbum());
        Bitmap cover = CoverLoader.getInstance().loadThumbnail(music);

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.notification);
        if (cover != null) {
            remoteViews.setImageViewBitmap(R.id.iv_icon, cover);
        } else {
            remoteViews.setImageViewResource(R.id.iv_icon, R.drawable.ic_launcher);
        }
        remoteViews.setTextViewText(R.id.tv_title, title);
        remoteViews.setTextViewText(R.id.tv_subtitle, subtitle);

        Intent playIntent = new Intent(StatusBarReceiver.ACTION_STATUS_BAR);
        playIntent.putExtra(StatusBarReceiver.EXTRA_ACTION, StatusBarReceiver.EXTRA_PLAY_PAUSE);
        PendingIntent playPendingIntent = PendingIntent.getBroadcast(context, 0, playIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setImageViewResource(R.id.iv_play_pause, AppCache.getPlayService().isPlaying() ? R.drawable.ic_status_bar_pause_dark_selector : R.drawable.ic_status_bar_play_dark_selector);
        remoteViews.setOnClickPendingIntent(R.id.iv_play_pause, playPendingIntent);

        Intent nextIntent = new Intent(StatusBarReceiver.ACTION_STATUS_BAR);
        nextIntent.putExtra(StatusBarReceiver.EXTRA_ACTION, StatusBarReceiver.EXTRA_NEXT);
        PendingIntent nextPendingIntent = PendingIntent.getBroadcast(context, 1, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setImageViewResource(R.id.iv_next, R.drawable.ic_status_bar_next_dark_selector);
        remoteViews.setOnClickPendingIntent(R.id.iv_next, nextPendingIntent);

        return remoteViews;
    }
}
