package me.wcy.music.search

import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.util.KeyboardUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import me.wcy.common.ext.viewBindings
import me.wcy.common.widget.pager.TabLayoutPager
import me.wcy.music.R
import me.wcy.music.common.BaseMusicFragment
import me.wcy.music.consts.RoutePath
import me.wcy.music.databinding.FragmentSearchBinding
import me.wcy.music.databinding.TitleSearchBinding
import me.wcy.music.search.playlist.SearchPlaylistFragment
import me.wcy.music.search.song.SearchSongFragment
import me.wcy.router.annotation.Route

/**
 * Created by wangchenyan.top on 2023/9/20.
 */
@Route(RoutePath.SEARCH)
@AndroidEntryPoint
class SearchFragment : BaseMusicFragment() {
    private val viewBinding by viewBindings<FragmentSearchBinding>()
    private val titleBinding by lazy {
        TitleSearchBinding.bind(getTitleLayout()!!.getContentView()!!)
    }
    private val viewModel by activityViewModels<SearchViewModel>()
    private val menuSearch by lazy {
        getTitleLayout()!!.addTextMenu("搜索", false)!!
    }

    override fun getRootView(): View {
        return viewBinding.root
    }

    override fun onLazyCreate() {
        super.onLazyCreate()

        initTitle()
        initTab()

        lifecycleScope.launch {
            viewModel.showResult.collectLatest { showResult ->
                viewBinding.llHistory.isVisible = showResult.not()
                viewBinding.llResult.isVisible = showResult
            }
        }
    }

    private fun initTitle() {
        menuSearch.setOnClickListener {
            val keywords = titleBinding.etSearch.text?.trim()?.toString() ?: ""
            if (keywords.isNotEmpty()) {
                KeyboardUtils.hideSoftInput(requireActivity())
                viewModel.search(keywords)
            }
        }
    }

    private fun initTab() {
        val pager = TabLayoutPager(
            lifecycle,
            childFragmentManager,
            viewBinding.viewPage2,
            viewBinding.tabLayout
        )
        pager.addFragment(SearchSongFragment(), "单曲")
        pager.addFragment(SearchPlaylistFragment(), "歌单")
        pager.setup()
    }

    override fun onInterceptBackEvent(): Boolean {
        if (viewModel.showResult.value) {
            viewModel.showHistory()
            return true
        }
        return super.onInterceptBackEvent()
    }

    override fun getNavigationBarColor(): Int {
        return R.color.play_bar_bg
    }
}