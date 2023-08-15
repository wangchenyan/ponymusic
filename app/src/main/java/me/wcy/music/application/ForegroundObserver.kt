package me.wcy.music.application

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import java.util.Collections

/**
 * Created by hzwangchenyan on 2017/9/20.
 */
class ForegroundObserver private constructor() : Application.ActivityLifecycleCallbacks {
    private val observerList: MutableList<Observer>
    private val handler: Handler
    private var isForeground = false
    private var resumeActivityCount = 0

    interface Observer {
        /**
         * 进入前台
         *
         * @param activity 当前处于栈顶的Activity
         */
        fun onForeground(activity: Activity?)

        /**
         * 进入后台
         *
         * @param activity 当前处于栈顶的Activity
         */
        fun onBackground(activity: Activity?)
    }

    private object SingletonHolder {
        val sInstance = ForegroundObserver()
    }

    init {
        observerList = Collections.synchronizedList(ArrayList())
        handler = Handler(Looper.getMainLooper())
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
    override fun onActivityStarted(activity: Activity) {}
    override fun onActivityResumed(activity: Activity) {
        resumeActivityCount++
        if (!isForeground && resumeActivityCount > 0) {
            isForeground = true
            // 从后台进入前台
            Log.i(TAG, "app in foreground")
            notify(activity, true)
        }
    }

    override fun onActivityPaused(activity: Activity) {
        resumeActivityCount--
        handler.postDelayed({
            if (isForeground && resumeActivityCount == 0) {
                isForeground = false
                // 从前台进入后台
                Log.i(TAG, "app in background")
                notify(activity, false)
            }
        }, CHECK_TASK_DELAY)
    }

    private fun notify(activity: Activity, foreground: Boolean) {
        for (observer in observerList) {
            if (foreground) {
                observer.onForeground(activity)
            } else {
                observer.onBackground(activity)
            }
        }
    }

    override fun onActivityStopped(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
    override fun onActivityDestroyed(activity: Activity) {}

    companion object {
        private const val TAG = "ForegroundObserver"
        private const val CHECK_TASK_DELAY: Long = 500
        fun init(application: Application) {
            application.registerActivityLifecycleCallbacks(getInstance())
        }

        private fun getInstance(): ForegroundObserver {
            return SingletonHolder.sInstance
        }

        fun addObserver(observer: Observer?) {
            if (observer == null) {
                return
            }
            if (getInstance().observerList.contains(observer)) {
                return
            }
            getInstance().observerList.add(observer)
        }

        fun removeObserver(observer: Observer?) {
            if (observer == null) {
                return
            }
            getInstance().observerList.remove(observer)
        }
    }
}