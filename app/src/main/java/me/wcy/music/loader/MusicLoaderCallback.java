package me.wcy.music.loader;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.webkit.ValueCallback;

import java.util.ArrayList;
import java.util.List;

import me.wcy.music.model.Music;
import me.wcy.music.storage.preference.Preferences;
import me.wcy.music.utils.CoverLoader;
import me.wcy.music.utils.ParseUtils;
import me.wcy.music.utils.SystemUtils;

public class MusicLoaderCallback implements LoaderManager.LoaderCallbacks {
    private final List<Music> musicList;
    private final Context context;
    private final ValueCallback<List<Music>> callback;

    public MusicLoaderCallback(Context context, ValueCallback<List<Music>> callback) {
        this.context = context;
        this.callback = callback;
        this.musicList = new ArrayList<>();
    }

    public Loader onCreateLoader(int id, Bundle args) {
        return new MusicCursorLoader(context);
    }

    public void onLoadFinished(Loader var1, Object var2) {
        this.onLoadFinished(var1, (Cursor) var2);
    }

    public void onLoaderReset(Loader loader) {
    }

    public void onLoadFinished(Loader loader, Cursor data) {
        if (data == null) {
            return;
        }

        long filterTime = ParseUtils.parseLong(Preferences.getFilterTime()) * 1000;
        long filterSize = ParseUtils.parseLong(Preferences.getFilterSize()) * 1024;

        int counter = 0;
        musicList.clear();
        while (data.moveToNext()) {
            // 是否为音乐，魅族手机上始终为0
            int isMusic = data.getInt(data.getColumnIndex(MediaStore.Audio.AudioColumns.IS_MUSIC));
            if (!SystemUtils.isFlyme() && isMusic == 0) {
                continue;
            }
            long duration = data.getLong(data.getColumnIndex(MediaStore.Audio.Media.DURATION));
            if (duration < filterTime) {
                continue;
            }
            long fileSize = data.getLong(data.getColumnIndex(MediaStore.Audio.Media.SIZE));
            if (fileSize < filterSize) {
                continue;
            }

            long id = data.getLong(data.getColumnIndex(BaseColumns._ID));
            String title = data.getString(data.getColumnIndex(MediaStore.Audio.AudioColumns.TITLE));
            String artist = data.getString(data.getColumnIndex(MediaStore.Audio.AudioColumns.ARTIST));
            String album = data.getString(data.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM));
            long albumId = data.getLong(data.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM_ID));
            String path = data.getString(data.getColumnIndex(MediaStore.Audio.AudioColumns.DATA));
            String fileName = data.getString(data.getColumnIndex(MediaStore.Audio.AudioColumns.DISPLAY_NAME));

            Music music = new Music();
            music.setSongId(id);
            music.setType(Music.Type.LOCAL);
            music.setTitle(title);
            music.setArtist(artist);
            music.setAlbum(album);
            music.setAlbumId(albumId);
            music.setDuration(duration);
            music.setPath(path);
            music.setFileName(fileName);
            music.setFileSize(fileSize);
            if (++counter <= 20) {
                // 只加载前20首的缩略图
                CoverLoader.get().loadThumb(music);
            }
            musicList.add(music);
        }

        callback.onReceiveValue(musicList);
    }
}
