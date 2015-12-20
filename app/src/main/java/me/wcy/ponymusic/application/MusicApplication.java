package me.wcy.ponymusic.application;

import android.app.Application;

import com.squareup.okhttp.OkHttpClient;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.concurrent.TimeUnit;

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

        OkHttpClient client = OkHttpUtils.getInstance().getOkHttpClient();
        client.setConnectTimeout(1000 * 30, TimeUnit.MILLISECONDS);
    }

    public static MusicApplication getInstance() {
        return instance;
    }
}
