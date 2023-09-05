package me.wcy.music.common

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.util.LongSparseArray
import me.wcy.music.executor.DownloadMusicInfo
import me.wcy.music.model.Music

/**
 * Created by hzwangchenyan on 2016/11/23.
 */
class AppCache private constructor() {
    private var mContext: Context? = null
    private val mLocalMusicList: MutableList<Music> = ArrayList()
    private val mActivityStack: MutableList<Activity> = ArrayList()
    private val mDownloadList = LongSparseArray<DownloadMusicInfo>()

    private object SingletonHolder {
        val instance = AppCache()
    }

    fun init(application: Application) {
        mContext = application.applicationContext
        application.registerActivityLifecycleCallbacks(ActivityLifecycle())
    }

    fun getContext(): Context? {
        return mContext
    }

    fun getLocalMusicList(): MutableList<Music> {
        return mLocalMusicList
    }

    fun clearStack() {
        val activityStack = mActivityStack
        for (i in activityStack.indices.reversed()) {
            val activity = activityStack[i]
            if (!activity.isFinishing) {
                activity.finish()
            }
        }
        activityStack.clear()
    }

    fun getDownloadList(): LongSparseArray<DownloadMusicInfo> {
        return mDownloadList
    }

    private inner class ActivityLifecycle : Application.ActivityLifecycleCallbacks {
        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            Log.i("Activity", "onCreate: " + activity.javaClass.simpleName)
            mActivityStack.add(activity)
        }

        override fun onActivityStarted(activity: Activity) {}
        override fun onActivityResumed(activity: Activity) {}
        override fun onActivityPaused(activity: Activity) {}
        override fun onActivityStopped(activity: Activity) {}
        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
        override fun onActivityDestroyed(activity: Activity) {
            Log.i("Activity", "onDestroy: " + activity.javaClass.simpleName)
            mActivityStack.remove(activity)
        }
    }

    companion object {
        fun get(): AppCache {
            return SingletonHolder.instance
        }
    }
}