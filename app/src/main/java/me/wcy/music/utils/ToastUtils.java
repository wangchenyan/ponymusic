package me.wcy.music.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Toast工具类
 * Created by wcy on 2015/12/26.
 */
public class ToastUtils {
    private static Context sContext;

    public static void setContext(Context context) {
        sContext = context.getApplicationContext();
    }

    public static void show(int resId) {
        Toast.makeText(sContext, resId, Toast.LENGTH_SHORT).show();
    }

    public static void show(String text) {
        Toast.makeText(sContext, text, Toast.LENGTH_SHORT).show();
    }
}
