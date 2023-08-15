package me.wcy.music.loader

import android.content.Context
import android.provider.MediaStore
import androidx.loader.content.CursorLoader

class MusicCursorLoader(context: Context) : CursorLoader(context) {
    private val proj = arrayOf(
        MediaStore.Audio.Media._ID,
        MediaStore.Audio.Media.IS_MUSIC,
        MediaStore.Audio.Media.TITLE,
        MediaStore.Audio.Media.ARTIST,
        MediaStore.Audio.Media.ALBUM,
        MediaStore.Audio.Media.ALBUM_ID,
        MediaStore.Audio.Media.DATA,
        MediaStore.Audio.Media.DISPLAY_NAME,
        MediaStore.Audio.Media.SIZE,
        MediaStore.Audio.Media.DURATION,
    )

    init {
        this.projection = proj
        this.uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        this.sortOrder = "${MediaStore.Audio.Media.DATE_MODIFIED} DESC"
        this.selection = "${MediaStore.Audio.Media.MIME_TYPE} = ?"
        this.selectionArgs = arrayOf("audio/mpeg")
    }
}