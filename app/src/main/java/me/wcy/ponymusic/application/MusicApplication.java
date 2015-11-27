package me.wcy.ponymusic.application;

import android.app.Application;

/**
 * 自定义Application
 * Created by wcy on 2015/11/27.
 */
public class MusicApplication extends Application {
    private static MusicApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static MusicApplication getInstance() {
        return instance;
    }
}
