package me.wcy.music.application

import android.app.Application

/**
 * 自定义Application
 * Created by wcy on 2015/11/27.
 */
class MusicApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AppCache.get().init(this)
        ForegroundObserver.init(this)
        // DBManager.get().init(this)
    }
}