package me.wcy.music.common.bean

import com.google.gson.annotations.SerializedName

/**
 * Created by wangchenyan.top on 2023/9/6.
 */
data class QualityData(
    @SerializedName("br")
    val br: Int = 0,
    @SerializedName("fid")
    val fid: Int = 0,
    @SerializedName("size")
    val size: Int = 0,
    @SerializedName("vd")
    val vd: Int = 0,
    @SerializedName("sr")
    val sr: Int = 0
)