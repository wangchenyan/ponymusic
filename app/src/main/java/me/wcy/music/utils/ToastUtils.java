package me.wcy.music.utils;

import android.widget.Toast;

import me.wcy.music.application.MusicApplication;

/**
 * Toast工具类
 * Created by wcy on 2015/12/26.
 */
public class ToastUtils {
    public static void show(int resId) {
        Toast.makeText(MusicApplication.getInstance().getApplicationContext(), resId, Toast.LENGTH_SHORT).show();
    }

    public static void show(String text) {
        Toast.makeText(MusicApplication.getInstance().getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }
}
