package me.wcy.music.mine.like

import com.google.gson.annotations.SerializedName

/**
 * Created by wangchenyan.top on 2023/9/26.
 */
data class LikeSongListData(
    @SerializedName("code")
    val code: Int = 0,
    @SerializedName("ids")
    val ids: List<Long> = emptyList(),
)
