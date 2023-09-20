package me.wcy.music.discover

import android.view.View
import me.wcy.common.ext.viewBindings
import me.wcy.music.R
import me.wcy.music.common.ApiDomainDialog
import me.wcy.music.common.BaseMusicFragment
import me.wcy.music.consts.RoutePath
import me.wcy.music.databinding.FragmentDiscoverBinding
import me.wcy.music.main.MainActivity
import me.wcy.music.storage.preference.MusicPreferences
import me.wcy.router.CRouter

/**
 * Created by wangchenyan.top on 2023/8/21.
 */
class DiscoverFragment : BaseMusicFragment() {
    private val viewBinding by viewBindings<FragmentDiscoverBinding>()

    override fun getRootView(): View {
        return viewBinding.root
    }

    override fun isUseLoadSir(): Boolean {
        return true
    }

    override fun getLoadSirTarget(): View {
        return viewBinding.content
    }

    override fun onReload() {
        super.onReload()
        checkApiDomain(true)
    }

    override fun onLazyCreate() {
        super.onLazyCreate()

        initTitle()
        checkApiDomain(false)
        viewBinding.btnRecommendSong.setOnClickListener {
            CRouter.with(requireActivity()).url(RoutePath.RECOMMEND_SONG).start()
        }
    }

    private fun initTitle() {
        getTitleLayout()?.run {
            addImageMenu(
                R.drawable.ic_menu,
                isDayNight = true,
                isLeft = true
            ).setOnClickListener {
                val activity = requireActivity()
                if (activity is MainActivity) {
                    activity.openDrawer()
                }
            }
        }
        getTitleLayout()?.getContentView()?.setOnClickListener {
            if (ApiDomainDialog.checkApiDomain(requireContext())) {
                CRouter.with(requireActivity()).url(RoutePath.SEARCH).start()
            }
        }
    }

    private fun checkApiDomain(isReload: Boolean) {
        if (MusicPreferences.apiDomain.isNotEmpty()) {
            showLoadSirSuccess()
        } else {
            showLoadSirError("请先设置云音乐API域名")
            if (isReload) {
                ApiDomainDialog.checkApiDomain(requireContext())
            }
        }
    }
}