package me.wcy.music.account.bean

import com.google.gson.annotations.SerializedName

data class SendCodeResult(
    @SerializedName("code")
    val code: Int = 0,
    @SerializedName("message")
    val message: String = "",
)
