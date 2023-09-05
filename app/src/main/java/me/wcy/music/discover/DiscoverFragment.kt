package me.wcy.music.discover

import android.view.View
import me.wcy.common.ext.viewBindings
import me.wcy.music.common.BaseMusicFragment
import me.wcy.music.databinding.FragmentDiscoverBinding

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

    override fun onLazyCreate() {
        super.onLazyCreate()
        showLoadSirLoading()
    }
}