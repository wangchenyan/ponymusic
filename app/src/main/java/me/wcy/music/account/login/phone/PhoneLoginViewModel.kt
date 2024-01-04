package me.wcy.music.account.login.phone

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import top.wangchenyan.common.ext.toUnMutable
import top.wangchenyan.common.model.CommonResult
import me.wcy.music.account.AccountApi
import me.wcy.music.account.service.UserService
import me.wcy.music.net.NetUtils
import javax.inject.Inject

/**
 * Created by wangchenyan.top on 2024/1/3.
 */
@HiltViewModel
class PhoneLoginViewModel @Inject constructor(
    private val userService: UserService
) : ViewModel() {
    private val _sendPhoneCodeCountdown = MutableStateFlow(0)
    val sendPhoneCodeCountdown = _sendPhoneCodeCountdown.toUnMutable()

    suspend fun sendPhoneCode(phone: String): CommonResult<Unit> {
        if (_sendPhoneCodeCountdown.value > 0) {
            return CommonResult.fail()
        }
        val res = kotlin.runCatching {
            AccountApi.get().sendPhoneCode(phone)
        }
        return if (res.isSuccess) {
            val data = res.getOrThrow()
            if (data.code == 200) {
                viewModelScope.launch {
                    _sendPhoneCodeCountdown.value = 30
                    repeat(Int.MAX_VALUE) {
                        delay(1000)
                        _sendPhoneCodeCountdown.value = _sendPhoneCodeCountdown.value - 1
                        if (_sendPhoneCodeCountdown.value == 0) {
                            return@launch
                        }
                    }
                }
                CommonResult.success(Unit)
            } else {
                CommonResult.fail(data.code, data.message)
            }
        } else {
            NetUtils.parseErrorResponse(res.exceptionOrNull())
        }
    }

    suspend fun phoneLogin(phone: String, code: String): CommonResult<Unit> {
        val loginRes = kotlin.runCatching {
            AccountApi.get().phoneLogin(phone, code)
        }
        return if (loginRes.isSuccess) {
            val data = loginRes.getOrNull()
            if (data?.code == 200) {
                val getProfileRes = userService.login(data.cookie)
                if (getProfileRes.isSuccessWithData()) {
                    CommonResult.success(Unit)
                } else {
                    CommonResult.fail(getProfileRes.code, getProfileRes.msg)
                }
            } else {
                CommonResult.fail(data?.code ?: -1, data?.message)
            }
        } else {
            var result = NetUtils.parseErrorResponse(loginRes.exceptionOrNull())
            if (result.code == -462) {
                result = result.copy(msg = "登录失败，请更新服务端版本或稍后重试")
            }
            return result
        }
    }
}