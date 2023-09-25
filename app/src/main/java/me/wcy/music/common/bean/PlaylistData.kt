package me.wcy.music.common.bean

import com.google.gson.annotations.SerializedName
import me.wcy.music.account.bean.ProfileData

data class PlaylistData(
    @SerializedName("id")
    val id: Long = 0,
    @SerializedName("name")
    val name: String = "",
    @SerializedName("coverImgUrl")
    val coverImgUrl: String = "",
    @SerializedName("creator")
    val creator: ProfileData = ProfileData(),
    @SerializedName("subscribed")
    val subscribed: Boolean = false,
    @SerializedName("trackCount")
    val trackCount: Int = 0,
    @SerializedName("userId")
    val userId: Int = 0,
    @SerializedName("playCount")
    val playCount: Int = 0,
    @SerializedName("bookCount")
    val bookCount: Int = 0,
    @SerializedName("specialType")
    val specialType: Int = 0,
    @SerializedName("description")
    val description: String = "",
    @SerializedName("tags")
    val tags: List<String> = emptyList(),
    @SerializedName("highQuality")
    val highQuality: Boolean = false
)