package me.wcy.music.account.bean

import com.google.gson.annotations.SerializedName

data class LoginResultData(
    @SerializedName("code")
    val code: Int = 0,
    @SerializedName("message")
    val message: String = "",
    @SerializedName("nickname")
    val nickname: String = "",
    @SerializedName("avatarUrl")
    val avatarUrl: String = "",
    @SerializedName("cookie")
    val cookie: String = ""
) {
    companion object {
        // 二维码不存在或已过期
        const val STATUS_INVALID = 800

        // 等待扫码
        const val STATUS_NOT_SCAN = 801

        // 授权中
        const val STATUS_SCANNING = 802

        // 授权登陆成功，包含 cookie
        const val STATUS_SUCCESS = 803
    }
}