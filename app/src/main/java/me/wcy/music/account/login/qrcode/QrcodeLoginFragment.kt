package me.wcy.music.account.login.qrcode

import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import top.wangchenyan.common.ext.viewBindings
import me.wcy.music.account.bean.LoginResultData
import me.wcy.music.account.login.LoginRouteFragment
import me.wcy.music.account.service.UserService
import me.wcy.music.common.BaseMusicFragment
import me.wcy.music.consts.RoutePath
import me.wcy.music.databinding.FragmentQrcodeLoginBinding
import me.wcy.router.annotation.Route
import javax.inject.Inject

/**
 * Created by wangchenyan.top on 2023/8/28.
 */
@Route(RoutePath.QRCODE_LOGIN)
@AndroidEntryPoint
class QrcodeLoginFragment : BaseMusicFragment() {
    private val viewBinding by viewBindings<FragmentQrcodeLoginBinding>()
    private val viewModel by viewModels<QrcodeLoginViewModel>()

    @Inject
    lateinit var userService: UserService

    override fun getRootView(): View {
        return viewBinding.root
    }

    override fun onLazyCreate() {
        super.onLazyCreate()

        viewBinding.tvPhoneLogin.setOnClickListener {
            activity?.apply {
                setResult(LoginRouteFragment.RESULT_SWITCH_PHONE)
                finish()
            }
        }

        loadQrCode()

        lifecycleScope.launch {
            viewModel.loginStatus.collectLatest { status ->
                viewBinding.tvStatus.setOnClickListener(null)
                if (status == null) {
                    viewBinding.tvStatus.isVisible = true
                    viewBinding.tvStatus.text = "加载中…"
                } else {
                    when (status.code) {
                        LoginResultData.STATUS_NOT_SCAN -> {
                            viewBinding.tvStatus.isVisible = false
                        }

                        LoginResultData.STATUS_SCANNING -> {
                            viewBinding.tvStatus.isVisible = true
                            viewBinding.tvStatus.text = "「${status.nickname}」授权中"
                        }

                        LoginResultData.STATUS_SUCCESS -> {
                            viewBinding.tvStatus.isVisible = true
                            viewBinding.tvStatus.text = status.message
                            getProfile(status.cookie)
                        }

                        LoginResultData.STATUS_INVALID -> {
                            viewBinding.tvStatus.isVisible = true
                            viewBinding.tvStatus.text = "二维码已失效，点击刷新"
                            viewBinding.tvStatus.setOnClickListener {
                                loadQrCode()
                            }
                        }

                        else -> {
                            viewBinding.tvStatus.isVisible = true
                            viewBinding.tvStatus.text =
                                status.message.ifEmpty { "二维码错误，点击刷新" }
                            viewBinding.tvStatus.setOnClickListener {
                                loadQrCode()
                            }
                        }
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.qrCode.collectLatest { qrCode ->
                viewBinding.ivQrCode.setImageBitmap(qrCode)
            }
        }
    }

    private fun loadQrCode() {
        lifecycleScope.launch {
            viewModel.getLoginQrCode()
        }
    }

    private fun getProfile(cookie: String) {
        lifecycleScope.launch {
            val res = userService.login(cookie)
            if (res.isSuccessWithData()) {
                setResultAndFinish()
            } else {
                viewBinding.tvStatus.isVisible = true
                viewBinding.tvStatus.text = "登录失败，点击重试"
                viewBinding.tvStatus.setOnClickListener {
                    loadQrCode()
                }
            }
        }
    }
}