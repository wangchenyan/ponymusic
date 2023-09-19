package me.wcy.music.account.service

import android.app.Activity
import kotlinx.coroutines.flow.StateFlow
import me.wcy.common.model.CommonResult
import me.wcy.music.account.bean.ProfileData

/**
 * Created by wangchenyan.top on 2023/9/18.
 */
interface IUserService {
    val profile: StateFlow<ProfileData?>

    fun getCookie(): String

    fun isLogin(): Boolean

    suspend fun login(cookie: String): CommonResult<ProfileData>

    suspend fun logout()

    fun checkLogin(
        activity: Activity,
        showDialog: Boolean = true,
        onCancel: (() -> Unit)? = null,
        onLogin: (() -> Unit)? = null
    )
}