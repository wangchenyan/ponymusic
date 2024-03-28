package me.wcy.music.utils

import android.net.Uri
import androidx.core.os.bundleOf
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import me.wcy.music.common.bean.SongData
import me.wcy.music.storage.db.entity.SongEntity
import top.wangchenyan.common.CommonApp

/**
 * Created by wangchenyan.top on 2023/9/18.
 */

const val SCHEME_NETEASE = "netease"
const val PARAM_ID = "id"
const val EXTRA_DURATION = "duration"
const val EXTRA_FILE_NAME = "file_name"
const val EXTRA_FILE_SIZE = "file_size"
const val EXTRA_SMALL_COVER = "small_cover"

fun SongData.getSimpleArtist(): String {
    return ar.joinToString("/") { it.name }
}

fun SongEntity.toMediaItem(): MediaItem {
    return MediaItem.Builder()
        .setMediaId(uniqueId)
        .setUri(path)
        .setMediaMetadata(
            MediaMetadata.Builder()
                .setTitle(title)
                .setArtist(artist)
                .setAlbumTitle(album)
                .setAlbumArtist(artist)
                .setArtworkUri(Uri.parse(getLargeCover()))
                .setSmallCover(getSmallCover())
                .setDuration(duration)
                .setFileName(fileName)
                .setFileSize(fileSize)
                .build()
        )
        .build()
}

fun MediaItem.toSongEntity(): SongEntity {
    return SongEntity(
        type = getSongType(),
        songId = getSongId(),
        title = mediaMetadata.title?.toString() ?: "",
        artist = mediaMetadata.artist?.toString() ?: "",
        artistId = 0,
        album = mediaMetadata.albumTitle?.toString() ?: "",
        albumId = 0,
        albumCover = mediaMetadata.artworkUri?.toString() ?: "",
        duration = mediaMetadata.getDuration(),
        path = localConfiguration?.uri?.toString() ?: "",
        fileName = mediaMetadata.getFileName(),
        fileSize = mediaMetadata.getFileSize()
    )
}

fun SongData.toMediaItem(): MediaItem {
    val uri = Uri.Builder()
        .scheme(SCHEME_NETEASE)
        .authority(CommonApp.app.packageName)
        .appendQueryParameter(PARAM_ID, id.toString())
        .build()
    return MediaItem.Builder()
        .setMediaId(generateUniqueId(SongEntity.ONLINE, id))
        .setUri(uri)
        .setMediaMetadata(
            MediaMetadata.Builder()
                .setTitle(name)
                .setArtist(getSimpleArtist())
                .setAlbumTitle(al.name)
                .setAlbumArtist(getSimpleArtist())
                .setArtworkUri(Uri.parse(al.getLargeCover()))
                .setSmallCover(al.getSmallCover())
                .setDuration(dt)
                .build()
        )
        .build()
}

fun generateUniqueId(type: Int, songId: Long): String {
    return "$type#$songId"
}

fun MediaItem.isLocal(): Boolean {
    return getSongType() == SongEntity.LOCAL
}

fun MediaItem.getSongType(): Int {
    return mediaId.split("#").firstOrNull()?.toIntOrNull() ?: SongEntity.LOCAL
}

fun MediaItem.getSongId(): Long {
    return mediaId.split("#").getOrNull(1)?.toLongOrNull() ?: 0L
}

fun MediaMetadata.Builder.setDuration(duration: Long) = apply {
    val extras = build().extras ?: bundleOf()
    extras.putLong(EXTRA_DURATION, duration)
    setExtras(extras)
}

fun MediaMetadata.getDuration(): Long {
    return extras?.getLong(EXTRA_DURATION) ?: 0
}

fun MediaMetadata.Builder.setFileName(name: String) = apply {
    val extras = build().extras ?: bundleOf()
    extras.putString(EXTRA_FILE_NAME, name)
    setExtras(extras)
}

fun MediaMetadata.getFileName(): String {
    return extras?.getString(EXTRA_FILE_NAME) ?: ""
}

fun MediaMetadata.Builder.setFileSize(size: Long) = apply {
    val extras = build().extras ?: bundleOf()
    extras.putLong(EXTRA_FILE_SIZE, size)
    setExtras(extras)
}

fun MediaMetadata.getFileSize(): Long {
    return extras?.getLong(EXTRA_FILE_SIZE) ?: 0
}

fun MediaMetadata.Builder.setSmallCover(value: String) = apply {
    val extras = build().extras ?: bundleOf()
    extras.putString(EXTRA_SMALL_COVER, value)
    setExtras(extras)
}

fun MediaMetadata.getSmallCover(): String {
    return extras?.getString(EXTRA_SMALL_COVER) ?: ""
}
