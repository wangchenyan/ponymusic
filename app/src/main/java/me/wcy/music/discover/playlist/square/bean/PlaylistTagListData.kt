package me.wcy.music.discover.playlist.square.bean

import com.google.gson.annotations.SerializedName

/**
 * Created by wangchenyan.top on 2023/9/26.
 */
data class PlaylistTagListData(
    @SerializedName("code")
    val code: Int = 0,
    @SerializedName("tags")
    val tags: List<PlaylistTagData> = emptyList(),
)
