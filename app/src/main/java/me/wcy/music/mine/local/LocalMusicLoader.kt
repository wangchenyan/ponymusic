package me.wcy.music.mine.local

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import me.wcy.music.storage.db.entity.SongEntity
import me.wcy.music.storage.preference.ConfigPreferences

/**
 * Created by wangchenyan.top on 2023/8/30.
 */
class LocalMusicLoader {
    private val projection = arrayOf(
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
    private val sortOrder = "${MediaStore.Audio.Media.DATE_MODIFIED} DESC"

    fun load(context: Context): List<SongEntity> {
        val result = mutableListOf<SongEntity>()
        val query = context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            sortOrder
        )

        query?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
            val albumIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
            val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            val displayNameColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
            val isMusicColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.IS_MUSIC)
            val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)

            val filterTime = ConfigPreferences.filterTime.toLong() * 1000
            val filterSize = ConfigPreferences.filterSize.toLong() * 1024

            while (cursor.moveToNext()) {
                val isMusic = cursor.getInt(isMusicColumn)
                if (isMusic == 0) {
                    continue
                }
                val duration = cursor.getLong(durationColumn)
                if (duration < filterTime) {
                    continue
                }
                val fileSize = cursor.getLong(sizeColumn)
                if (fileSize < filterSize) {
                    continue
                }
                val id = cursor.getLong(idColumn)
                val title = cursor.getString(titleColumn)
                val artist = cursor.getString(artistColumn)
                val album = cursor.getString(albumColumn)
                val albumId = cursor.getLong(albumIdColumn)

                val artworkUri = Uri.parse("content://media/external/audio/albumart")
                val albumCover = ContentUris.withAppendedId(artworkUri, albumId)

                val uri =
                    Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id.toString())

                val path = cursor.getString(dataColumn)
                val fileName = cursor.getString(displayNameColumn)

                val entity = SongEntity(
                    type = SongEntity.LOCAL,
                    songId = id,
                    title = title,
                    artist = artist,
                    album = album,
                    albumId = albumId,
                    albumCover = albumCover.toString(),
                    duration = duration,
                    uri = uri.toString(),
                    path = path,
                    fileName = fileName,
                    fileSize = fileSize,
                )
                result.add(entity)
            }
        }

        return result
    }
}