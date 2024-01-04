package me.wcy.music.account.login.qrcode

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.bertsir.zbar.utils.QRUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import top.wangchenyan.common.ext.toUnMutable
import top.wangchenyan.common.net.apiCall
import me.wcy.music.account.AccountApi
import me.wcy.music.account.bean.LoginResultData
import me.wcy.music.account.service.UserService
import javax.inject.Inject

/**
 * Created by wangchenyan.top on 2023/8/28.
 */
@HiltViewModel
class QrcodeLoginViewModel @Inject constructor(
    private val userService: UserService
) : ViewModel() {
    private var qrCodeKey = ""
    private val _qrCode = MutableStateFlow<Bitmap?>(null)
    val qrCode = _qrCode
    private val _loginStatus = MutableStateFlow<LoginResultData?>(null)
    val loginStatus = _loginStatus.toUnMutable()
    private var job: Job? = null

    fun getLoginQrCode() {
        job?.cancel()
        job = viewModelScope.launch(Dispatchers.Default) {
            qrCodeKey = ""
            _qrCode.value = null
            _loginStatus.value = null
            val getKeyRes = apiCall {
                AccountApi.get().getQrCodeKey()
            }
            if (getKeyRes.isSuccessWithData().not()) {
                _loginStatus.value = LoginResultData(-1)
                return@launch
            }
            val keyData = getKeyRes.getDataOrThrow()
            qrCodeKey = keyData.unikey
            val getQrCodeRes = apiCall {
                AccountApi.get().getLoginQrCode(qrCodeKey)
            }
            if (getQrCodeRes.isSuccessWithData().not()) {
                _loginStatus.value = LoginResultData(-1)
                return@launch
            }
            val qrCodeData = getQrCodeRes.getDataOrThrow()
            _qrCode.value = QRUtils.getInstance().createQRCode(qrCodeData.qrurl)

            while (true) {
                kotlin.runCatching {
                    AccountApi.get().checkLoginStatus(qrCodeKey)
                }.onSuccess { status ->
                    _loginStatus.value = status
                    if (status.code == LoginResultData.STATUS_NOT_SCAN
                        || status.code == LoginResultData.STATUS_SCANNING
                    ) {
                        delay(3000)
                    } else {
                        return@launch
                    }
                }.onFailure {
                    _loginStatus.value = LoginResultData(-1, it.message ?: "")
                    return@launch
                }
            }
        }
    }
}