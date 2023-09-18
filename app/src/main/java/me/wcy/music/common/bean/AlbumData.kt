package me.wcy.music.common.bean

import com.google.gson.annotations.SerializedName

/**
 * Created by wangchenyan.top on 2023/9/6.
 */
data class AlbumData(
    @SerializedName("id")
    val id: Long = 0,
    @SerializedName("name")
    val name: String = "",
    @SerializedName("picUrl")
    val picUrl: String = "",
    @SerializedName("tns")
    val tns: List<Any> = listOf(),
    @SerializedName("pic_str")
    val picStr: String = "",
    @SerializedName("pic")
    val pic: Long = 0
)
