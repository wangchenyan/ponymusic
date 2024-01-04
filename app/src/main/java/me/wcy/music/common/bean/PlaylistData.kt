package me.wcy.music.common.bean

import com.google.gson.annotations.SerializedName
import me.wcy.music.account.bean.ProfileData
import me.wcy.music.utils.MusicUtils.asLargeCover
import me.wcy.music.utils.MusicUtils.asSmallCover

data class PlaylistData(
    @SerializedName("id")
    val id: Long = 0,
    @SerializedName("name")
    val name: String = "",
    @Deprecated("Please use resized url")
    @SerializedName("coverImgUrl", alternate = ["picUrl"])
    val coverImgUrl: String = "",
    @SerializedName("creator")
    val creator: ProfileData = ProfileData(),
    @SerializedName("subscribed")
    val subscribed: Boolean = false,
    @SerializedName("trackCount")
    val trackCount: Int = 0,
    @SerializedName("userId")
    val userId: Long = 0,
    @SerializedName("playCount", alternate = ["playcount"])
    val playCount: Long = 0,
    @SerializedName("bookCount")
    val bookCount: Long = 0,
    @SerializedName("specialType")
    val specialType: Int = 0,
    @SerializedName("description")
    val description: String = "",
    @SerializedName("tags")
    val tags: List<String> = emptyList(),
    @SerializedName("highQuality")
    val highQuality: Boolean = false,
    @SerializedName("updateFrequency")
    val updateFrequency: String = "",
    @SerializedName("ToplistType")
    val toplistType: String = "",
) {
    @SerializedName("_songList")
    var songList: List<SongData> = emptyList()

    fun getSmallCover(): String {
        return coverImgUrl.asSmallCover()
    }

    fun getLargeCover(): String {
        return coverImgUrl.asLargeCover()
    }
}