package me.wcy.music.application;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.v4.util.LongSparseArray;
import android.util.DisplayMetrics;

import me.wcy.music.executor.CrashHandler;
import me.wcy.music.utils.Preferences;
import me.wcy.music.utils.ScreenUtils;
import me.wcy.music.utils.ToastUtils;

/**
 * Created by hzwangchenyan on 2016/11/23.
 */
public class AppCache {
    private Context mContext;
    private LongSparseArray<String> mDownloadList = new LongSparseArray<>();

    private AppCache() {
    }

    private static class SingletonHolder {
        private static AppCache sAppCache = new AppCache();
    }

    private static AppCache getInstance() {
        return SingletonHolder.sAppCache;
    }

    public static void init(Context context) {
        if (getContext() != null) {
            return;
        }
        getInstance().onInit(context);
    }

    private void onInit(Context context) {
        mContext = context.getApplicationContext();
        ToastUtils.init(mContext);
        Preferences.init(mContext);
        ScreenUtils.init(mContext);
        CrashHandler.getInstance().init();
    }

    public static Context getContext() {
        return getInstance().mContext;
    }

    public static LongSparseArray<String> getDownloadList() {
        return getInstance().mDownloadList;
    }

    public static void updateNightMode(boolean on) {
        Resources resources = getContext().getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
        config.uiMode &= ~Configuration.UI_MODE_NIGHT_MASK;
        config.uiMode |= on ? Configuration.UI_MODE_NIGHT_YES : Configuration.UI_MODE_NIGHT_NO;
        resources.updateConfiguration(config, dm);
    }
}
