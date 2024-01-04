package me.wcy.music.account.login.phone

import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import top.wangchenyan.common.ext.viewBindings
import top.wangchenyan.common.utils.ToastUtils
import me.wcy.music.account.login.LoginRouteFragment
import me.wcy.music.account.service.UserService
import me.wcy.music.common.BaseMusicFragment
import me.wcy.music.consts.RoutePath
import me.wcy.music.databinding.FragmentPhoneLoginBinding
import me.wcy.router.annotation.Route
import javax.inject.Inject

/**
 * Created by wangchenyan.top on 2024/1/3.
 */
@Route(RoutePath.PHONE_LOGIN)
@AndroidEntryPoint
class PhoneLoginFragment : BaseMusicFragment() {
    private val viewBinding by viewBindings<FragmentPhoneLoginBinding>()
    private val viewModel by viewModels<PhoneLoginViewModel>()

    @Inject
    lateinit var userService: UserService

    override fun getRootView(): View {
        return viewBinding.root
    }

    override fun onLazyCreate() {
        super.onLazyCreate()

        initView()
        initDataObserver()
    }

    private fun initView() {
        val updateLoginBtnState = {
            viewBinding.btnLogin.isEnabled =
                viewBinding.etPhone.length() > 0 && viewBinding.etPhoneCode.length() > 0
        }
        viewBinding.etPhone.doAfterTextChanged {
            updateLoginBtnState()
        }
        viewBinding.etPhoneCode.doAfterTextChanged {
            updateLoginBtnState()
        }
        viewBinding.tvSendCode.setOnClickListener {
            val phone = viewBinding.etPhone.text?.toString()
            if (phone.isNullOrEmpty()) {
                ToastUtils.show("请输入手机号")
                return@setOnClickListener
            }
            lifecycleScope.launch {
                viewBinding.tvSendCode.isEnabled = false
                val res = viewModel.sendPhoneCode(phone)
                if (res.isSuccess().not()) {
                    viewBinding.tvSendCode.isEnabled = true
                    ToastUtils.show(res.msg)
                }
            }
        }
        viewBinding.btnLogin.setOnClickListener {
            val phone = viewBinding.etPhone.text?.toString()
            if (phone.isNullOrEmpty()) {
                ToastUtils.show("请输入手机号")
                return@setOnClickListener
            }
            val code = viewBinding.etPhoneCode.text?.toString()
            if (code.isNullOrEmpty()) {
                ToastUtils.show("请输入手机验证码")
                return@setOnClickListener
            }
            lifecycleScope.launch {
                showLoading(false)
                val res = viewModel.phoneLogin(phone, code)
                dismissLoading()
                if (res.isSuccess()) {
                    ToastUtils.show("登录成功")
                    setResultAndFinish()
                } else {
                    ToastUtils.show(res.msg.orEmpty().ifEmpty {
                        "登录失败，请更新服务端版本或稍后重试"
                    })
                }
            }
        }
        viewBinding.tvQrcodeLogin.setOnClickListener {
            activity?.apply {
                setResult(LoginRouteFragment.RESULT_SWITCH_QRCODE)
                finish()
            }
        }
    }

    private fun initDataObserver() {
        lifecycleScope.launch {
            viewModel.sendPhoneCodeCountdown.collectLatest { sendPhoneCodeCountdown ->
                if (sendPhoneCodeCountdown > 0) {
                    viewBinding.tvSendCode.isEnabled = false
                    viewBinding.tvSendCode.text = "${sendPhoneCodeCountdown}秒后重发"
                } else {
                    viewBinding.tvSendCode.isEnabled = true
                    viewBinding.tvSendCode.text = "获取验证码"
                }
            }
        }
    }
}