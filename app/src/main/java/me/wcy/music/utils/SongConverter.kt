package me.wcy.music.utils

import me.wcy.music.common.bean.SongData
import me.wcy.music.storage.db.entity.SongEntity

/**
 * Created by wangchenyan.top on 2023/9/18.
 */

fun SongData.getSimpleArtist(): String {
    return ar.joinToString("/") { it.name }
}

fun SongData.toEntity(): SongEntity {
    return SongEntity(
        type = SongEntity.ONLINE,
        songId = id,
        title = name,
        artist = getSimpleArtist(),
        artistId = ar.firstOrNull()?.id ?: 0,
        album = al.name,
        albumId = al.id,
        albumCover = al.picUrl,
        duration = dt,
    )
}