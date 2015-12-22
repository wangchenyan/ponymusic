package me.wcy.ponymusic.application;

import android.app.Application;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
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

        // OkHttp
        OkHttpClient client = OkHttpUtils.getInstance().getOkHttpClient();
        client.setConnectTimeout(1000 * 30, TimeUnit.MILLISECONDS);

        // ImageLoader
        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(this)
                .memoryCacheSize(2 * 1024 * 1024)
                .diskCacheSize(50 * 1024 * 1024)
                .build();
        ImageLoader.getInstance().init(configuration);
    }

    public static MusicApplication getInstance() {
        return instance;
    }
}
