package me.wcy.music.loader

import android.content.Context
import android.content.CursorLoader
import android.provider.MediaStore

/**
 * @author wcy
 * @date 2018/7/11
 */
class MusicCursorLoader(context: Context) : CursorLoader(context) {
    private val proj = arrayOf(
            MediaStore.Audio.AudioColumns._ID,
            MediaStore.Audio.AudioColumns.IS_MUSIC,
            MediaStore.Audio.AudioColumns.TITLE,
            MediaStore.Audio.AudioColumns.ARTIST,
            MediaStore.Audio.AudioColumns.ALBUM,
            MediaStore.Audio.AudioColumns.ALBUM_ID,
            MediaStore.Audio.AudioColumns.DATA,
            MediaStore.Audio.AudioColumns.DISPLAY_NAME,
            MediaStore.Audio.AudioColumns.SIZE,
            MediaStore.Audio.AudioColumns.DURATION)

    init {
        projection = proj
        uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        sortOrder = MediaStore.Audio.Media.DATE_MODIFIED + " desc"
        selection = MediaStore.Audio.Media.MIME_TYPE + "= ?"
        selectionArgs = arrayOf("audio/mpeg")
    }
}