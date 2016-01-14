package me.wcy.music.utils;

import android.content.Context;
import android.text.TextUtils;
import android.view.WindowManager;

import com.nostra13.universalimageloader.core.DisplayImageOptions;

import me.wcy.music.R;
import me.wcy.music.application.MusicApplication;

/**
 * 工具类
 * Created by hzwangchenyan on 2016/1/6.
 */
public class Utils {
    public static int getScreenWidth() {
        WindowManager wm = (WindowManager) MusicApplication.getInstance().getSystemService(Context.WINDOW_SERVICE);
        return wm.getDefaultDisplay().getWidth();
    }

    /**
     * 获取状态栏高度
     */
    public static int getSystemBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static String getArtistAndAlbum(String artist, String album) {
        if (TextUtils.isEmpty(artist) && TextUtils.isEmpty(album)) {
            return "";
        } else if (!TextUtils.isEmpty(artist) && TextUtils.isEmpty(album)) {
            return artist;
        } else if (TextUtils.isEmpty(artist) && !TextUtils.isEmpty(album)) {
            return album;
        } else {
            return artist + " - " + album;
        }
    }

    public static DisplayImageOptions getDefaultDisplayImageOptions() {
        return new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.ic_default_cover)
                .showImageForEmptyUri(R.drawable.ic_default_cover)
                .showImageOnFail(R.drawable.ic_default_cover)
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .build();
    }

    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }
}
