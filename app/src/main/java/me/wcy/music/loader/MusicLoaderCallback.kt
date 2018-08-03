package me.wcy.music.loader

import android.app.LoaderManager
import android.content.Context
import android.content.Loader
import android.database.Cursor
import android.os.Bundle
import android.provider.BaseColumns
import android.provider.MediaStore
import android.webkit.ValueCallback
import me.wcy.music.model.Music
import me.wcy.music.storage.preference.Preferences
import me.wcy.music.utils.CoverLoader
import me.wcy.music.utils.ParseUtils
import me.wcy.music.utils.SystemUtils

/**
 * @author wcy
 * @date 2018/7/11
 */
class MusicLoaderCallback(private val context: Context, private val callback: ValueCallback<List<Music>>) : LoaderManager.LoaderCallbacks<Cursor> {
    private val musicList = mutableListOf<Music>()

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        return MusicCursorLoader(context)
    }

    override fun onLoadFinished(loader: Loader<Cursor>?, data: Cursor?) {
        if (data == null) {
            return
        }

        val filterTime = ParseUtils.parseLong(Preferences.getFilterTime()) * 1000
        val filterSize = ParseUtils.parseLong(Preferences.getFilterSize()) * 1024

        var counter = 0
        musicList.clear()
        while (data.moveToNext()) {
            // 是否为音乐，魅族手机上始终为0
            val isMusic = data.getInt(data.getColumnIndex(MediaStore.Audio.AudioColumns.IS_MUSIC))
            if (!SystemUtils.isFlyme() && isMusic == 0) {
                continue
            }
            val duration = data.getLong(data.getColumnIndex(MediaStore.Audio.Media.DURATION))
            if (duration < filterTime) {
                continue
            }
            val fileSize = data.getLong(data.getColumnIndex(MediaStore.Audio.Media.SIZE))
            if (fileSize < filterSize) {
                continue
            }

            val id = data.getLong(data.getColumnIndex(BaseColumns._ID))
            val title = data.getString(data.getColumnIndex(MediaStore.Audio.AudioColumns.TITLE))
            val artist = data.getString(data.getColumnIndex(MediaStore.Audio.AudioColumns.ARTIST))
            val album = data.getString(data.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM))
            val albumId = data.getLong(data.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM_ID))
            val path = data.getString(data.getColumnIndex(MediaStore.Audio.AudioColumns.DATA))
            val fileName = data.getString(data.getColumnIndex(MediaStore.Audio.AudioColumns.DISPLAY_NAME))

            val music = Music()
            music.songId = id
            music.type = Music.Type.LOCAL
            music.title = title
            music.artist = artist
            music.album = album
            music.albumId = albumId
            music.duration = duration
            music.path = path
            music.fileName = fileName
            music.fileSize = fileSize
            if (++counter <= 20) {
                // 只加载前20首的缩略图
                CoverLoader.get().loadThumb(music)
            }
            musicList.add(music)
        }

        callback.onReceiveValue(musicList)
    }

    override fun onLoaderReset(loader: Loader<Cursor>?) {
    }
}