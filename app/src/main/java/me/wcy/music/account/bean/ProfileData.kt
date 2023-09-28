package me.wcy.music.account.bean

import com.google.gson.annotations.SerializedName

/**
 * Created by wangchenyan.top on 2023/8/28.
 */
data class ProfileData(
    @SerializedName("userId")
    val userId: Long = 0,
    @SerializedName("userType")
    val userType: Int = 0,
    @SerializedName("nickname")
    val nickname: String = "",
    @SerializedName("avatarImgId")
    val avatarImgId: Long = 0,
    @SerializedName("avatarUrl")
    val avatarUrl: String = "",
    @SerializedName("backgroundImgId")
    val backgroundImgId: Long = 0,
    @SerializedName("backgroundUrl")
    val backgroundUrl: String = "",
    @SerializedName("signature")
    val signature: String = "",
    @SerializedName("createTime")
    val createTime: Long = 0,
    @SerializedName("userName")
    val userName: String = "",
    @SerializedName("accountType")
    val accountType: Int = 0,
    @SerializedName("shortUserName")
    val shortUserName: String = "",
    @SerializedName("birthday")
    val birthday: Long = 0,
    @SerializedName("authority")
    val authority: Int = 0,
    @SerializedName("gender")
    val gender: Int = 0,
    @SerializedName("accountStatus")
    val accountStatus: Int = 0,
    @SerializedName("province")
    val province: Int = 0,
    @SerializedName("city")
    val city: Int = 0,
    @SerializedName("authStatus")
    val authStatus: Int = 0,
    @SerializedName("defaultAvatar")
    val defaultAvatar: Boolean = false,
    @SerializedName("djStatus")
    val djStatus: Int = 0,
    @SerializedName("locationStatus")
    val locationStatus: Int = 0,
    @SerializedName("vipType")
    val vipType: Int = 0,
    @SerializedName("followed")
    val followed: Boolean = false,
    @SerializedName("mutual")
    val mutual: Boolean = false,
    @SerializedName("authenticated")
    val authenticated: Boolean = false,
    @SerializedName("lastLoginTime")
    val lastLoginTime: Long = 0,
    @SerializedName("lastLoginIP")
    val lastLoginIP: String = "",
    @SerializedName("viptypeVersion")
    val viptypeVersion: Long = 0,
    @SerializedName("authenticationTypes")
    val authenticationTypes: Int = 0,
    @SerializedName("anchor")
    val anchor: Boolean = false
)
