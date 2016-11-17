package me.wcy.music.utils;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.wcy.music.R;
import me.wcy.music.application.MusicApplication;
import me.wcy.music.constants.Constants;
import me.wcy.music.model.Music;

/**
 * 文件工具类
 * Created by wcy on 2016/1/3.
 */
public class FileUtils {
    private static String getAppDir() {
        return Environment.getExternalStorageDirectory() + "/PonyMusic/";
    }

    public static String getMusicDir() {
        String dir = getAppDir() + "Music/";
        return mkdirs(dir);
    }

    public static String getLrcDir() {
        String dir = getAppDir() + "Lyric/";
        return mkdirs(dir);
    }

    public static String getLogDir() {
        String dir = getAppDir() + "Log/";
        return mkdirs(dir);
    }

    public static String getSplashDir(Context context) {
        String dir = context.getFilesDir() + "/splash/";
        return mkdirs(dir);
    }

    public static String getRelativeMusicDir() {
        String dir = "PonyMusic/Music/";
        return mkdirs(dir);
    }

    /**
     * 获取歌词路径
     * 先从已下载文件夹中查找，如果不存在，则从歌曲文件所在文件夹查找
     */
    public static String getLrcFilePath(Music music) {
        String lrcFilePath = getLrcDir() + music.getFileName().replace(Constants.FILENAME_MP3, Constants.FILENAME_LRC);
        if (!new File(lrcFilePath).exists()) {
            lrcFilePath = music.getUri().replace(Constants.FILENAME_MP3, Constants.FILENAME_LRC);
        }
        return lrcFilePath;
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

    public static float b2mb(int b) {
        DecimalFormat decimalFormat = new DecimalFormat(".00");
        String MB = decimalFormat.format((float) b / 1024 / 1024);
        return Float.valueOf(MB);
    }
}
