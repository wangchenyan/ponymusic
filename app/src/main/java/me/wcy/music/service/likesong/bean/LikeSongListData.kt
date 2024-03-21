package me.wcy.music.service.likesong.bean

import com.google.gson.annotations.SerializedName

/**
 * Created by wangchenyan.top on 2024/3/21.
 */
data class LikeSongListData(
    @SerializedName("code")
    val code: Int = 0,
    @SerializedName("ids")
    val ids: Set<Long> = emptySet()
)
