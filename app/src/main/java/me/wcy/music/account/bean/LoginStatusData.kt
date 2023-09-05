package me.wcy.music.account.bean

import com.google.gson.annotations.SerializedName

data class LoginStatusData(
    @SerializedName("data")
    val `data`: Data = Data()
) {
    data class Data(
        @SerializedName("code")
        val code: Int = 0,
        @SerializedName("account")
        val account: Account = Account(),
        @SerializedName("profile")
        val profile: ProfileData? = null
    ) {
        data class Account(
            @SerializedName("id")
            val id: Int = 0,
            @SerializedName("userName")
            val userName: String = "",
            @SerializedName("type")
            val type: Int = 0,
            @SerializedName("status")
            val status: Int = 0,
            @SerializedName("whitelistAuthority")
            val whitelistAuthority: Int = 0,
            @SerializedName("createTime")
            val createTime: Long = 0,
            @SerializedName("tokenVersion")
            val tokenVersion: Int = 0,
            @SerializedName("ban")
            val ban: Int = 0,
            @SerializedName("baoyueVersion")
            val baoyueVersion: Int = 0,
            @SerializedName("donateVersion")
            val donateVersion: Int = 0,
            @SerializedName("vipType")
            val vipType: Int = 0,
            @SerializedName("anonimousUser")
            val anonimousUser: Boolean = false,
            @SerializedName("paidFee")
            val paidFee: Boolean = false
        )
    }
}