package me.wcy.music.common.bean

import com.google.gson.annotations.SerializedName

/**
 * Created by wangchenyan.top on 2023/9/6.
 */
data class OriginSongSimpleData(
    @SerializedName("songId")
    val songId: Int = 0,
    @SerializedName("name")
    val name: String = "",
    @SerializedName("artists")
    val artists: List<ArtistData> = listOf(),
    @SerializedName("albumMeta")
    val albumMeta: AlbumData = AlbumData()
)