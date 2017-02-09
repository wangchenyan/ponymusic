package me.wcy.music.application;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.v4.util.LongSparseArray;
import android.util.DisplayMetrics;

import com.amap.api.location.AMapLocalWeatherLive;

import java.util.ArrayList;
import java.util.List;

import me.wcy.music.activity.BaseActivity;
import me.wcy.music.model.Music;
import me.wcy.music.model.SongListInfo;
import me.wcy.music.service.PlayService;
import me.wcy.music.utils.Preferences;
import me.wcy.music.utils.ScreenUtils;
import me.wcy.music.utils.ToastUtils;

/**
 * Created by hzwangchenyan on 2016/11/23.
 */
public class AppCache {
    private Context mContext;
    private PlayService mPlayService;
    // 本地歌曲列表
    private final List<Music> mMusicList = new ArrayList<>();
    // 歌单列表
    private final List<SongListInfo> mSongListInfos = new ArrayList<>();
    private final List<BaseActivity> mActivityStack = new ArrayList<>();
    private final LongSparseArray<String> mDownloadList = new LongSparseArray<>();
    private AMapLocalWeatherLive mAMapLocalWeatherLive;

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

    public static PlayService getPlayService() {
        return getInstance().mPlayService;
    }

    public static void setPlayService(PlayService service) {
        getInstance().mPlayService = service;
    }

    public static void updateNightMode(boolean on) {
        Resources resources = getContext().getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
        config.uiMode &= ~Configuration.UI_MODE_NIGHT_MASK;
        config.uiMode |= on ? Configuration.UI_MODE_NIGHT_YES : Configuration.UI_MODE_NIGHT_NO;
        resources.updateConfiguration(config, dm);
    }

    public static List<Music> getMusicList() {
        return getInstance().mMusicList;
    }

    public static List<SongListInfo> getSongListInfos() {
        return getInstance().mSongListInfos;
    }

    public static void addToStack(BaseActivity activity) {
        getInstance().mActivityStack.add(activity);
    }

    public static void removeFromStack(BaseActivity activity) {
        getInstance().mActivityStack.remove(activity);
    }

    public static void clearStack() {
        List<BaseActivity> activityStack = getInstance().mActivityStack;
        for (int i = activityStack.size() - 1; i >= 0; i--) {
            BaseActivity activity = activityStack.get(i);
            activityStack.remove(activity);
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }

    public static LongSparseArray<String> getDownloadList() {
        return getInstance().mDownloadList;
    }

    public static AMapLocalWeatherLive getAMapLocalWeatherLive() {
        return getInstance().mAMapLocalWeatherLive;
    }

    public static void setAMapLocalWeatherLive(AMapLocalWeatherLive aMapLocalWeatherLive) {
        getInstance().mAMapLocalWeatherLive = aMapLocalWeatherLive;
    }
}
