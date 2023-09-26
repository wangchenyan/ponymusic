package me.wcy.music.discover.playlist.square.bean

import com.google.gson.annotations.SerializedName

data class PlaylistTagData(
    @SerializedName("id")
    val id: Long = 0,
    @SerializedName("name")
    val name: String = "",
    @SerializedName("activity")
    val activity: Boolean = false,
    @SerializedName("hot")
    val hot: Boolean = false,
    @SerializedName("position")
    val position: Int = 0,
    @SerializedName("category")
    val category: Int = 0,
    @SerializedName("createTime")
    val createTime: Long = 0,
    @SerializedName("usedCount")
    val usedCount: Long = 0,
    @SerializedName("type")
    val type: Int = 0
)