package me.wcy.music.loader

import android.content.Context
import android.database.Cursor
import android.os.Bundle
import android.provider.BaseColumns
import android.provider.MediaStore
import android.webkit.ValueCallback
import androidx.loader.app.LoaderManager
import androidx.loader.content.Loader
import me.wcy.music.model.Music
import me.wcy.music.storage.preference.MusicPreferences
import me.wcy.music.utils.CoverLoader

class MusicLoaderCallback(
    private val context: Context,
    private val callback: ValueCallback<List<Music>>
) : LoaderManager.LoaderCallbacks<Cursor> {
    private val musicList: MutableList<Music>

    init {
        musicList = ArrayList()
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        return MusicCursorLoader(context)
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
        this.onLoadFinished(data)
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
    }

    private fun onLoadFinished(data: Cursor?) {
        if (data == null) {
            return
        }
        val filterTime = MusicPreferences.filterTime.toLong() * 1000
        val filterSize = MusicPreferences.filterSize.toLong() * 1024
        var counter = 0
        musicList.clear()
        while (data.moveToNext()) {
            // 是否为音乐，魅族手机上始终为0
            val isMusic = data.getInt(data.getColumnIndexOrThrow(MediaStore.Audio.Media.IS_MUSIC))
            if (isMusic == 0) {
                continue
            }
            val duration = data.getLong(data.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION))
            if (duration < filterTime) {
                continue
            }
            val fileSize = data.getLong(data.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE))
            if (fileSize < filterSize) {
                continue
            }
            val id = data.getLong(data.getColumnIndexOrThrow(BaseColumns._ID))
            val title = data.getString(data.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE))
            val artist = data.getString(data.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST))
            val album = data.getString(data.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM))
            val albumId = data.getLong(data.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID))
            val path = data.getString(data.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA))
            val fileName =
                data.getString(data.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME))
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
}