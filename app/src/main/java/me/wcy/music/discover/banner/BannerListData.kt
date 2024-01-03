package me.wcy.music.discover.banner

import com.google.gson.annotations.SerializedName

/**
 * Created by wangchenyan.top on 2024/1/3.
 */
data class BannerListData(
    @SerializedName("code")
    val code: Int = 0,
    @SerializedName("banners")
    val banners: List<BannerData> = emptyList(),
)