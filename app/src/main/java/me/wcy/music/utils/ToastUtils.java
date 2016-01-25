package me.wcy.music.utils;

import android.content.Context;
import android.widget.Toast;

import me.wcy.music.application.MusicApplication;

/**
 * Toast工具类
 * Created by wcy on 2015/12/26.
 */
public class ToastUtils {
    public static void show(int resId) {
        Context context = MusicApplication.getInstance().getApplicationContext();
        Toast.makeText(context, resId, Toast.LENGTH_SHORT).show();
    }

    public static void show(String text) {
        Context context = MusicApplication.getInstance().getApplicationContext();
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }
}
