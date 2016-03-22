package me.wcy.music.utils;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 * Created by hzwangchenyan on 2016/3/22.
 */
public class SystemUtils {

    public static boolean isStackResumed(Context context) {
        ActivityManager manager = (ActivityManager) context.getApplicationContext().getSystemService(
                Context.ACTIVITY_SERVICE);
        String packageName = context.getApplicationContext().getPackageName();
        List<ActivityManager.RunningTaskInfo> recentTaskInfos = manager.getRunningTasks(1);
        if (recentTaskInfos != null && recentTaskInfos.size() > 0) {
            ActivityManager.RunningTaskInfo taskInfo = recentTaskInfos.get(0);
            if (taskInfo.baseActivity.getPackageName().equals(packageName) &&
                    taskInfo.numActivities > 1) {
                return true;
            }
        }
        return false;
    }
}
