package me.wcy.music.discover.ranking

import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewbinding.ViewBinding
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.SizeUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import me.wcy.music.R
import me.wcy.music.common.BaseMusicFragment
import me.wcy.music.common.bean.PlaylistData
import me.wcy.music.consts.RoutePath
import me.wcy.music.databinding.FragmentRankingBinding
import me.wcy.music.discover.DiscoverApi
import me.wcy.music.discover.ranking.item.OfficialRankingItemBinder
import me.wcy.music.discover.ranking.item.RankingTitleItemBinding
import me.wcy.music.discover.ranking.item.SelectedRankingItemBinder
import me.wcy.music.discover.ranking.viewmodel.RankingViewModel
import me.wcy.music.service.PlayerController
import me.wcy.music.utils.toMediaItem
import me.wcy.radapter3.RAdapter
import me.wcy.radapter3.RItemBinder
import me.wcy.radapter3.RTypeMapper
import me.wcy.router.CRouter
import me.wcy.router.annotation.Route
import top.wangchenyan.common.ext.viewBindings
import javax.inject.Inject

/**
 * Created by wangchenyan.top on 2023/10/25.
 */
@Route(RoutePath.RANKING)
@AndroidEntryPoint
class RankingFragment : BaseMusicFragment() {
    private val viewBinding by viewBindings<FragmentRankingBinding>()
    private val viewModel by viewModels<RankingViewModel>()
    private val adapter by lazy { RAdapter<Any>() }

    @Inject
    lateinit var playerController: PlayerController

    override fun getRootView(): View {
        return viewBinding.root
    }

    override fun isUseLoadSir(): Boolean {
        return true
    }

    override fun getLoadSirTarget(): View {
        return viewBinding.recyclerView
    }

    override fun onReload() {
        super.onReload()
        loadData()
    }

    override fun onLazyCreate() {
        super.onLazyCreate()

        initView()
        initDataObserver()
        loadData()
    }

    private fun loadData() {
        lifecycleScope.launch {
            showLoadSirLoading()
            val res = viewModel.loadData()
            if (res.isSuccess()) {
                showLoadSirSuccess()
            } else {
                showLoadSirError(res.msg)
            }
        }
    }

    private fun initView() {
        val itemWidth = (ScreenUtils.getAppScreenWidth() - SizeUtils.dp2px(52f)) / 3
        adapter.register(PlaylistData::class, object : RTypeMapper<PlaylistData> {
            private val officialItemBinder = OfficialRankingItemBinder(object :
                OfficialRankingItemBinder.OnItemClickListener {
                override fun onItemClick(item: PlaylistData, position: Int) {
                    openRankingDetail(item)
                }

                override fun onPlayClick(item: PlaylistData, position: Int) {
                    playPlaylist(item)
                }
            })
            private val selectedItemBinder = SelectedRankingItemBinder(itemWidth,
                object : SelectedRankingItemBinder.OnItemClickListener {
                    override fun onItemClick(item: PlaylistData, position: Int) {
                        openRankingDetail(item)
                    }

                    override fun onPlayClick(item: PlaylistData, position: Int) {
                        playPlaylist(item)
                    }

                    override fun getFirstSelectedPosition(): Int {
                        val dataList = viewModel.rankingList.value ?: return -1
                        return dataList.indexOfFirst { it is PlaylistData && it.toplistType.isEmpty() }
                    }
                })

            override fun map(data: PlaylistData): RItemBinder<out ViewBinding, PlaylistData> {
                return if (data.toplistType.isNotEmpty()) {
                    officialItemBinder
                } else {
                    selectedItemBinder
                }
            }
        })
        adapter.register(RankingTitleItemBinding())
        viewBinding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 3).apply {
            spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    val dataList = viewModel.rankingList.value ?: return 1
                    val item = dataList.getOrNull(position) ?: return 1
                    return if (item is RankingViewModel.TitleData
                        || (item is PlaylistData && item.toplistType.isNotEmpty())
                    ) {
                        3
                    } else {
                        1
                    }
                }
            }
        }
        viewBinding.recyclerView.adapter = adapter
    }

    private fun initDataObserver() {
        viewModel.rankingList.observe(this) { rankingList ->
            rankingList ?: return@observe
            adapter.refresh(rankingList)
        }
    }

    private fun openRankingDetail(item: PlaylistData) {
        CRouter.with(requireActivity())
            .url(RoutePath.PLAYLIST_DETAIL)
            .extra("id", item.id)
            .start()
    }

    private fun playPlaylist(playlistData: PlaylistData) {
        lifecycleScope.launch {
            showLoading()
            kotlin.runCatching {
                DiscoverApi.getFullPlaylistSongList(playlistData.id)
            }.onSuccess { songListData ->
                dismissLoading()
                if (songListData.code == 200 && songListData.songs.isNotEmpty()) {
                    val songs = songListData.songs.map { it.toMediaItem() }
                    playerController.replaceAll(songs, songs[0])
                }
            }.onFailure {
                dismissLoading()
            }
        }
    }

    override fun getNavigationBarColor(): Int {
        return R.color.play_bar_bg
    }
}