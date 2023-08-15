package me.wcy.music.utils

import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.audiofx.AudioEffect
import android.net.Uri
import android.provider.BaseColumns
import android.provider.MediaStore
import me.wcy.music.model.Music
import me.wcy.music.storage.preference.Preferences

/**
 * 歌曲工具类
 * Created by wcy on 2015/11/27.
 */
object MusicUtils {
    private const val SELECTION =
        MediaStore.Audio.AudioColumns.SIZE + " >= ? AND " + MediaStore.Audio.AudioColumns.DURATION + " >= ?"

    /**
     * 扫描歌曲
     */
    fun scanMusic(context: Context): List<Music> {
        val musicList: MutableList<Music> = ArrayList()
        val filterSize = ParseUtils.parseLong(Preferences.filterSize) * 1024
        val filterTime = ParseUtils.parseLong(Preferences.filterTime) * 1000
        val cursor = context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            arrayOf(
                BaseColumns._ID,
                MediaStore.Audio.AudioColumns.IS_MUSIC,
                MediaStore.Audio.AudioColumns.TITLE,
                MediaStore.Audio.AudioColumns.ARTIST,
                MediaStore.Audio.AudioColumns.ALBUM,
                MediaStore.Audio.AudioColumns.ALBUM_ID,
                MediaStore.Audio.AudioColumns.DATA,
                MediaStore.Audio.AudioColumns.DISPLAY_NAME,
                MediaStore.Audio.AudioColumns.SIZE,
                MediaStore.Audio.AudioColumns.DURATION
            ),
            SELECTION,
            arrayOf(filterSize.toString(), filterTime.toString()),
            MediaStore.Audio.Media.DEFAULT_SORT_ORDER
        ) ?: return musicList
        var i = 0
        while (cursor.moveToNext()) {
            // 是否为音乐，魅族手机上始终为0
            val isMusic =
                cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.IS_MUSIC))
            if (!SystemUtils.isFlyme && isMusic == 0) {
                continue
            }
            val id = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID))
            val title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.TITLE))
            val artist =
                cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ARTIST))
            val album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM))
            val albumId =
                cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM_ID))
            val duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
            val path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATA))
            val fileName =
                cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DISPLAY_NAME))
            val fileSize = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE))
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
            if (++i <= 20) {
                // 只加载前20首的缩略图
                CoverLoader.get().loadThumb(music)
            }
            musicList.add(music)
        }
        cursor.close()
        return musicList
    }

    fun getMediaStoreAlbumCoverUri(albumId: Long): Uri {
        val artworkUri = Uri.parse("content://media/external/audio/albumart")
        return ContentUris.withAppendedId(artworkUri, albumId)
    }

    fun isAudioControlPanelAvailable(context: Context): Boolean {
        return isIntentAvailable(
            context,
            Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL)
        )
    }

    private fun isIntentAvailable(context: Context, intent: Intent): Boolean {
        return context.packageManager.resolveActivity(
            intent,
            PackageManager.GET_RESOLVED_FILTER
        ) != null
    }
}