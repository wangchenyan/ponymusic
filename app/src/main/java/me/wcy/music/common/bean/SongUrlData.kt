package me.wcy.music.common.bean

import com.google.gson.annotations.SerializedName

data class SongUrlData(
    @SerializedName("id")
    val id: Long = 0,
    @SerializedName("url")
    val url: String = "",
    @SerializedName("br")
    val br: Int = 0,
    @SerializedName("size")
    val size: Int = 0,
    @SerializedName("md5")
    val md5: String = "",
    @SerializedName("code")
    val code: Int = 0,
    @SerializedName("expi")
    val expi: Int = 0,
    @SerializedName("type")
    val type: String = "",
    @SerializedName("gain")
    val gain: Double = 0.0,
    @SerializedName("peak")
    val peak: Int = 0,
    @SerializedName("fee")
    val fee: Int = 0,
    @SerializedName("payed")
    val payed: Int = 0,
    @SerializedName("flag")
    val flag: Int = 0,
    @SerializedName("canExtend")
    val canExtend: Boolean = false,
    @SerializedName("level")
    val level: String = "",
    @SerializedName("encodeType")
    val encodeType: String = "",
    @SerializedName("urlSource")
    val urlSource: Int = 0,
    @SerializedName("rightSource")
    val rightSource: Int = 0,
    @SerializedName("time")
    val time: Int = 0
)