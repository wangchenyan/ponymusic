package me.wcy.ponymusic.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.WindowManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import me.wcy.ponymusic.application.MusicApplication;
import me.wcy.ponymusic.model.MusicInfo;

/**
 * 歌曲工具类
 * Created by wcy on 2015/11/27.
 */
public class MusicUtils {
    // 存放歌曲列表
    private static List<MusicInfo> sMusicList = new ArrayList<>();

    /**
     * 扫描歌曲
     */
    public static void scanMusic(Context context) {
        sMusicList.clear();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null,
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        if (cursor == null) {
            return;
        }
        while (cursor.moveToNext()) {
            // 是否为音乐
            int isMusic = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC));
            if (isMusic == 0) {
                continue;
            }
            long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
            String title = cursor.getString((cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
            String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
            String album = cursor.getString((cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)));
            long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
            String url = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
            long albumId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
            String coverUri = getCoverUri(context, albumId);
            String fileName = cursor.getString((cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)));
            MusicInfo musicInfo = new MusicInfo();
            musicInfo.setId(id);
            musicInfo.setTitle(title);
            musicInfo.setArtist(artist);
            musicInfo.setAlbum(album);
            musicInfo.setDuration(duration);
            musicInfo.setUri(url);
            musicInfo.setCoverUri(coverUri);
            musicInfo.setFileName(fileName);
            sMusicList.add(musicInfo);
        }
        cursor.close();
    }

    private static String getCoverUri(Context context, long albumId) {
        String result = "";
        Cursor cursor = context.getContentResolver().query(
                Uri.parse("content://media/external/audio/albums/" + albumId),
                new String[]{"album_art"}, null, null, null);
        if (cursor != null) {
            cursor.moveToNext();
            result = cursor.getString(0);
            cursor.close();
        }
        return result;
    }

    public static List<MusicInfo> getMusicList() {
        return sMusicList;
    }

    public static int getScreenWidth() {
        WindowManager wm = (WindowManager) MusicApplication.getInstance().getSystemService(Context.WINDOW_SERVICE);
        return wm.getDefaultDisplay().getWidth();
    }

    private static String getAppDir() {
        return Environment.getExternalStorageDirectory() + File.separator + "PonyMusic" + File.separator;
    }

    public static String getLrcDir() {
        String dir = getAppDir() + "Lyric" + File.separator;
        return mkdirs(dir);
    }

    public static String getMusicDir() {
        String dir = getAppDir() + "Music" + File.separator;
        return mkdirs(dir);
    }

    private static String mkdirs(String dir) {
        File file = new File(dir);
        if (!file.exists()) {
            file.mkdirs();
        }
        return dir;
    }
}
