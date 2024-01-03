package me.wcy.music.discover.banner

import com.google.gson.annotations.SerializedName
import me.wcy.music.common.bean.SongData

data class BannerData(
    @SerializedName("pic")
    val pic: String = "",
    @SerializedName("targetId")
    val targetId: Long = 0,
    @SerializedName("targetType")
    val targetType: Int = 0,
    @SerializedName("titleColor")
    val titleColor: String = "",
    @SerializedName("typeTitle")
    val typeTitle: String = "",
    @SerializedName("url")
    val url: String = "",
    @SerializedName("exclusive")
    val exclusive: Boolean = false,
    @SerializedName("encodeId")
    val encodeId: String = "",
    @SerializedName("song")
    val song: SongData? = null,
    @SerializedName("bannerId")
    val bannerId: String = "",
    @SerializedName("alg")
    val alg: String = "",
    @SerializedName("scm")
    val scm: String = "",
    @SerializedName("requestId")
    val requestId: String = "",
    @SerializedName("showAdTag")
    val showAdTag: Boolean = false,
    @SerializedName("s_ctrp")
    val sCtrp: String = "",
    @SerializedName("bannerBizType")
    val bannerBizType: String = ""
)