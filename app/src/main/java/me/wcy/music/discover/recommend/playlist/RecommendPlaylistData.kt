package me.wcy.music.discover.recommend.playlist

import com.google.gson.annotations.SerializedName
import me.wcy.music.common.bean.PlaylistData

/**
 * Created by wangchenyan.top on 2023/9/25.
 */
data class RecommendPlaylistData(
    @SerializedName("code")
    val code: Int = 0,
    @SerializedName("recommend")
    val recommend: List<PlaylistData> = emptyList(),
)
