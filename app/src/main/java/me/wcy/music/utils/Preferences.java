package me.wcy.music.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import me.wcy.music.application.MusicApplication;

/**
 * SharedPreferences工具类
 * Created by wcy on 2015/11/28.
 */
public class Preferences {
    public static final String MUSIC_ID = "music_id";
    public static final String PLAY_MODE = "play_mode";

    public static void put(String key, Object value) {
        Context context = MusicApplication.getInstance().getApplicationContext();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        if (value instanceof Boolean) {
            editor.putBoolean(key, (Boolean) value);
        } else if (value instanceof Integer) {
            editor.putInt(key, (Integer) value);
        } else if (value instanceof Float) {
            editor.putFloat(key, (Float) value);
        } else if (value instanceof Long) {
            editor.putLong(key, (Long) value);
        } else if (value instanceof String) {
            editor.putString(key, (String) value);
        }
        editor.apply();
    }

    public static Object get(String key, Object defValue) {
        Context context = MusicApplication.getInstance().getApplicationContext();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        Object value = null;
        if (defValue instanceof Boolean) {
            value = sp.getBoolean(key, (Boolean) defValue);
        } else if (defValue instanceof Integer) {
            value = sp.getInt(key, (Integer) defValue);
        } else if (defValue instanceof Float) {
            value = sp.getFloat(key, (Float) defValue);
        } else if (defValue instanceof Long) {
            value = sp.getLong(key, (Long) defValue);
        } else if (defValue instanceof String) {
            value = sp.getString(key, (String) defValue);
        }
        return value;
    }
}
