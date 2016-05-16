package me.wcy.music.application;

import android.app.Application;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.v4.util.LongSparseArray;
import android.util.DisplayMetrics;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.concurrent.TimeUnit;

import im.fir.sdk.FIR;
import me.wcy.music.executor.CrashHandler;
import me.wcy.music.utils.Preferences;
import me.wcy.music.utils.ToastUtils;

/**
 * 自定义Application
 * Created by wcy on 2015/11/27.
 */
public class MusicApplication extends Application {
    private static MusicApplication sInstance;
    private LongSparseArray<String> mDownloadList = new LongSparseArray<>();
    private static Resources sRes;

    @Override
    public void onCreate() {
        super.onCreate();

        sInstance = this;
        sRes = getResources();
        ToastUtils.init(this);
        Preferences.init(this);
        updateNightMode(Preferences.isNightMode());
        CrashHandler.getInstance().init();
        initOkHttpUtils();
        initImageLoader();
        FIR.init(this);
    }

    public static MusicApplication getInstance() {
        return sInstance;
    }

    public LongSparseArray<String> getDownloadList() {
        return mDownloadList;
    }

    private void initOkHttpUtils() {
        OkHttpUtils.getInstance().setConnectTimeout(30, TimeUnit.SECONDS);
        OkHttpUtils.getInstance().setReadTimeout(30, TimeUnit.SECONDS);
        OkHttpUtils.getInstance().setWriteTimeout(30, TimeUnit.SECONDS);
    }

    private void initImageLoader() {
        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(this)
                .memoryCacheSize(2 * 1024 * 1024) // 2MB
                .diskCacheSize(50 * 1024 * 1024) // 50MB
                .build();
        ImageLoader.getInstance().init(configuration);
    }

    public static void updateNightMode(boolean on) {
        DisplayMetrics dm = sRes.getDisplayMetrics();
        Configuration config = sRes.getConfiguration();
        config.uiMode &= ~Configuration.UI_MODE_NIGHT_MASK;
        config.uiMode |= on ? Configuration.UI_MODE_NIGHT_YES : Configuration.UI_MODE_NIGHT_NO;
        sRes.updateConfiguration(config, dm);
    }
}
