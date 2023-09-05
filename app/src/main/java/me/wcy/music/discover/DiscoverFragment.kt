package me.wcy.music.discover

import android.view.View
import me.wcy.common.ext.viewBindings
import me.wcy.common.ui.fragment.BaseFragment
import me.wcy.music.databinding.FragmentDiscoverBinding

/**
 * Created by wangchenyan.top on 2023/8/21.
 */
class DiscoverFragment : BaseFragment() {
    private val viewBinding by viewBindings<FragmentDiscoverBinding>()

    override fun getRootView(): View {
        return viewBinding.root
    }
}