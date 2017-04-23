package me.wcy.music.application;

import android.app.Application;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.tencent.bugly.Bugly;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.log.LoggerInterceptor;

import java.util.concurrent.TimeUnit;

import me.wcy.music.BuildConfig;
import me.wcy.music.api.KeyStore;
import me.wcy.music.http.HttpInterceptor;
import me.wcy.music.utils.Preferences;
import okhttp3.OkHttpClient;

/**
 * 自定义Application
 * Created by wcy on 2015/11/27.
 */
public class MusicApplication extends Application {
    private static final String TAG = "PonyMusic";

    @Override
    public void onCreate() {
        super.onCreate();

        AppCache.init(this);
        AppCache.updateNightMode(Preferences.isNightMode());
        initOkHttpUtils();
        initImageLoader();
        initBugly();
    }

    private void initOkHttpUtils() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .addInterceptor(new HttpInterceptor())
                .addInterceptor(new LoggerInterceptor(TAG, true))
                .build();
        OkHttpUtils.initClient(okHttpClient);
    }

    private void initImageLoader() {
        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(this)
                .memoryCacheSize(2 * 1024 * 1024) // 2MB
                .diskCacheSize(50 * 1024 * 1024) // 50MB
                .build();
        ImageLoader.getInstance().init(configuration);
    }

    private void initBugly() {
        if (!BuildConfig.DEBUG) {
            Bugly.init(this, KeyStore.getKey(KeyStore.BUGLY_APP_ID), false);
        }
    }
}
