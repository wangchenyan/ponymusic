package me.wcy.music.common.bean

import com.google.gson.annotations.SerializedName

data class LrcData(
    @SerializedName("version")
    val version: Int = 0,
    @SerializedName("lyric")
    val lyric: String = ""
) {
    fun isValid() = lyric.isNotEmpty()
}