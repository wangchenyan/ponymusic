package me.wcy.music.utils

import android.app.ActivityManager
import android.app.Service
import android.content.Context
import android.text.TextUtils
import android.text.format.DateUtils
import java.util.Locale

/**
 * Created by hzwangchenyan on 2016/3/22.
 */
object SystemUtils {
    /**
     * 判断是否有Activity在运行
     */
    fun isStackResumed(context: Context): Boolean {
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningTaskInfos = manager.getRunningTasks(1)
        val runningTaskInfo = runningTaskInfos[0]
        return runningTaskInfo.numActivities > 1
    }

    /**
     * 判断Service是否在运行
     */
    fun isServiceRunning(context: Context, serviceClass: Class<out Service?>): Boolean {
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }

    val isFlyme: Boolean
        get() {
            val flymeFlag = getSystemProperty("ro.build.display.id")
            return !TextUtils.isEmpty(flymeFlag) && flymeFlag!!.lowercase(Locale.getDefault())
                .contains("flyme")
        }

    private fun getSystemProperty(key: String): String? {
        try {
            val classType = Class.forName("android.os.SystemProperties")
            val getMethod = classType.getDeclaredMethod("get", String::class.java)
            return getMethod.invoke(classType, key) as String
        } catch (th: Throwable) {
            th.printStackTrace()
        }
        return null
    }

    fun formatTime(pattern: String, milli: Long): String {
        val m = (milli / DateUtils.MINUTE_IN_MILLIS).toInt()
        val s = (milli / DateUtils.SECOND_IN_MILLIS % 60).toInt()
        val mm = String.format(Locale.getDefault(), "%02d", m)
        val ss = String.format(Locale.getDefault(), "%02d", s)
        return pattern.replace("mm", mm).replace("ss", ss)
    }
}