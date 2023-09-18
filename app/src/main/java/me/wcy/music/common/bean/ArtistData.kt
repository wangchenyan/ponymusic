package me.wcy.music.common.bean

import com.google.gson.annotations.SerializedName

/**
 * Created by wangchenyan.top on 2023/9/6.
 */
data class ArtistData(
    @SerializedName("id")
    val id: Long = 0,
    @SerializedName("name")
    val name: String = "",
    @SerializedName("tns")
    val tns: List<Any> = listOf(),
    @SerializedName("alias")
    val alias: List<Any> = listOf()
)