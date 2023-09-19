package me.wcy.music.common.bean

import com.google.gson.annotations.SerializedName

/**
 * Created by wangchenyan.top on 2023/9/18.
 */
data class LrcDataWrap(
    @SerializedName("code")
    val code: Int = -1,
    @SerializedName("lrc")
    val lrc: LrcData = LrcData()
)
