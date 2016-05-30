package me.wcy.music.utils;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;

import java.util.List;

import me.wcy.music.R;
import me.wcy.music.activity.BaseActivity;
import me.wcy.music.activity.SplashActivity;
import me.wcy.music.model.Music;

/**
 * Created by hzwangchenyan on 2016/3/22.
 */
public class SystemUtils {

    /**
     * 判断是否有Activity在运行
     */
    public static boolean isStackResumed(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1);
        ActivityManager.RunningTaskInfo runningTaskInfo = runningTaskInfos.get(0);
        return runningTaskInfo.numActivities > 1;
    }

    /**
     * 判断Service是否在运行
     */
    public static boolean isServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static Notification createNotification(Context context, Music music) {
        String title = music.getTitle();
        String subtitle = FileUtils.getArtistAndAlbum(music.getArtist(), music.getAlbum());
        Bitmap cover;
        if (music.getType() == Music.Type.LOCAL) {
            cover = CoverLoader.getInstance().loadThumbnail(music.getCoverUri());
        } else {
            cover = music.getCover();
        }
        Intent intent = new Intent(context, SplashActivity.class);
        intent.putExtra(Extras.FROM_NOTIFICATION, true);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        Notification.Builder builder = new Notification.Builder(context)
                .setContentIntent(pendingIntent)
                .setContentTitle(title)
                .setContentText(subtitle)
                .setSmallIcon(R.drawable.ic_notification)
                .setLargeIcon(cover);
        return builder.getNotification();
    }

    public static void clearStack(List<BaseActivity> activityStack) {
        for (int i = activityStack.size() - 1; i >= 0; i--) {
            BaseActivity activity = activityStack.get(i);
            activityStack.remove(activity);
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }

    public static String formatTime(String pattern, long milli) {
        int m = (int) (milli / (60 * 1000));
        int s = (int) ((milli / 1000) % 60);
        String mm = String.format("%02d", m);
        String ss = String.format("%02d", s);
        return pattern.replace("mm", mm).replace("ss", ss);
    }
}
