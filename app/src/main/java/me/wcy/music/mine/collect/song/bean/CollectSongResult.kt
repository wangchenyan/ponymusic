package me.wcy.music.mine.collect.song.bean

import com.google.gson.annotations.SerializedName

/**
 * Created by wangchenyan.top on 2024/3/21.
 */
data class CollectSongResult(
    @SerializedName("status")
    val status: Int = 0,
    @SerializedName("body")
    val body: Body = Body(),
) {
    data class Body(
        @SerializedName("code")
        val code: Int = 0,
        @SerializedName("message")
        val message: String = "",
    )
}
