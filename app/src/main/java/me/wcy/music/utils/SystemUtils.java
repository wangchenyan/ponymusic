package me.wcy.music.utils;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 * Created by hzwangchenyan on 2016/3/22.
 */
public class SystemUtils {

    public static boolean isStackResumed(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1);
        ActivityManager.RunningTaskInfo runningTaskInfo = runningTaskInfos.get(0);
        return runningTaskInfo.numActivities > 1;
    }
}
