package me.wcy.music.utils;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.wcy.music.R;
import me.wcy.music.application.MusicApplication;

/**
 * 文件工具类
 * Created by wcy on 2016/1/3.
 */
public class FileUtils {
    private static String getAppDir() {
        return Environment.getExternalStorageDirectory() + File.separator + "PonyMusic" + File.separator;
    }

    public static String getMusicDir() {
        String dir = getAppDir() + "Music" + File.separator;
        return mkdirs(dir);
    }

    public static String getLrcDir() {
        String dir = getAppDir() + "Lyric" + File.separator;
        return mkdirs(dir);
    }

    public static String getLogDir() {
        String dir = getAppDir() + "Log" + File.separator;
        return mkdirs(dir);
    }

    public static String getSplashDir(Context context) {
        String dir = context.getFilesDir() + File.separator + "splash" + File.separator;
        return mkdirs(dir);
    }

    public static String getRelativeMusicDir() {
        String dir = "PonyMusic" + File.separator + "Music" + File.separator;
        return mkdirs(dir);
    }

    private static String mkdirs(String dir) {
        File file = new File(dir);
        if (!file.exists()) {
            file.mkdirs();
        }
        return dir;
    }

    public static String getMp3FileName(String artist, String title) {
        artist = stringFilter(artist);
        title = stringFilter(title);
        if (TextUtils.isEmpty(artist)) {
            artist = MusicApplication.getInstance().getString(R.string.unknown);
        }
        if (TextUtils.isEmpty(title)) {
            title = MusicApplication.getInstance().getString(R.string.unknown);
        }
        return artist + " - " + title + Constants.FILENAME_MP3;
    }

    public static String getLrcFileName(String artist, String title) {
        artist = stringFilter(artist);
        title = stringFilter(title);
        if (TextUtils.isEmpty(artist)) {
            artist = MusicApplication.getInstance().getString(R.string.unknown);
        }
        if (TextUtils.isEmpty(title)) {
            title = MusicApplication.getInstance().getString(R.string.unknown);
        }
        return artist + " - " + title + Constants.FILENAME_LRC;
    }

    /**
     * 过滤特殊字符(\/:*?"<>|)
     */
    private static String stringFilter(String str) {
        if (str == null) {
            return null;
        }
        String regEx = "[\\/:*?\"<>|]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }
}
