package me.wcy.music.discover.home

import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.SizeUtils
import com.youth.banner.adapter.BannerImageAdapter
import com.youth.banner.config.IndicatorConfig
import com.youth.banner.holder.BannerImageHolder
import com.youth.banner.indicator.CircleIndicator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import me.wcy.music.R
import me.wcy.music.account.service.UserService
import me.wcy.music.common.ApiDomainDialog
import me.wcy.music.common.BaseMusicFragment
import me.wcy.music.common.bean.PlaylistData
import me.wcy.music.consts.RoutePath
import me.wcy.music.databinding.FragmentDiscoverBinding
import me.wcy.music.discover.DiscoverApi
import me.wcy.music.discover.banner.BannerData
import me.wcy.music.discover.home.viewmodel.DiscoverViewModel
import me.wcy.music.discover.playlist.square.item.PlaylistItemBinder
import me.wcy.music.discover.ranking.discover.item.DiscoverRankingItemBinder
import me.wcy.music.main.MainActivity
import me.wcy.music.service.PlayerController
import me.wcy.music.storage.preference.ConfigPreferences
import me.wcy.music.utils.toMediaItem
import me.wcy.radapter3.RAdapter
import me.wcy.router.CRouter
import top.wangchenyan.common.ext.load
import top.wangchenyan.common.ext.toast
import top.wangchenyan.common.ext.viewBindings
import top.wangchenyan.common.utils.LaunchUtils
import top.wangchenyan.common.widget.decoration.SpacingDecoration
import javax.inject.Inject

/**
 * Created by wangchenyan.top on 2023/8/21.
 */
@AndroidEntryPoint
class DiscoverFragment : BaseMusicFragment() {
    private val viewBinding by viewBindings<FragmentDiscoverBinding>()
    private val viewModel by viewModels<DiscoverViewModel>()

    private val recommendPlaylistAdapter by lazy {
        RAdapter<PlaylistData>()
    }
    private val rankingListAdapter by lazy {
        RAdapter<PlaylistData>()
    }

    @Inject
    lateinit var userService: UserService

    @Inject
    lateinit var playerController: PlayerController

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
        initBanner()
        initTopButton()
        initRecommendPlaylist()
        initRankingList()
        checkApiDomain(false)
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

    private fun initBanner() {
        viewBinding.banner.addBannerLifecycleObserver(this)
            .setIndicator(CircleIndicator(requireContext()))
            .setIndicatorGravity(IndicatorConfig.Direction.LEFT)
            .setIndicatorMargins(IndicatorConfig.Margins().apply {
                leftMargin = SizeUtils.dp2px(28f)
            })
            .setAdapter(object : BannerImageAdapter<BannerData>(emptyList()) {
                override fun onBindView(
                    holder: BannerImageHolder?,
                    data: BannerData?,
                    position: Int,
                    size: Int
                ) {
                    holder?.imageView?.apply {
                        val padding = SizeUtils.dp2px(16f)
                        setPadding(padding, 0, padding, 0)
                        load(data?.pic ?: "", SizeUtils.dp2px(12f))
                        setOnClickListener {
                            data ?: return@setOnClickListener
                            if (data.song != null) {
                                playerController.addAndPlay(data.song.toMediaItem())
                                CRouter.with(context).url(RoutePath.PLAYING).start()
                            } else if (data.url.isNotEmpty()) {
                                LaunchUtils.launchBrowser(requireContext(), data.url)
                            } else if (data.targetId > 0) {
                                CRouter.with(requireActivity())
                                    .url(RoutePath.PLAYLIST_DETAIL)
                                    .extra("id", data.targetId)
                                    .start()
                            }
                        }
                    }
                }
            })
        lifecycleScope.launch {
            viewModel.bannerList.collectLatest {
                viewBinding.banner.isVisible = it.isNotEmpty()
                viewBinding.bannerPlaceholder.isVisible = it.isEmpty()
                if (it.isNotEmpty()) {
                    viewBinding.banner.setDatas(it)
                }
            }
        }
    }

    private fun initTopButton() {
        viewBinding.btnRecommendSong.setOnClickListener {
            CRouter.with(requireActivity()).url(RoutePath.RECOMMEND_SONG).start()
        }
        viewBinding.btnPrivateFm.setOnClickListener {
            toast("敬请期待")
        }
        viewBinding.btnRecommendPlaylist.setOnClickListener {
            CRouter.with(requireActivity())
                .url(RoutePath.PLAYLIST_SQUARE)
                .start()
        }
        viewBinding.btnRank.setOnClickListener {
            CRouter.with(requireActivity()).url(RoutePath.RANKING).start()
        }
    }

    private fun initRecommendPlaylist() {
        viewBinding.tvRecommendPlaylist.setOnClickListener {
            CRouter.with(requireActivity())
                .url(RoutePath.PLAYLIST_SQUARE)
                .start()
        }
        val itemWidth = (ScreenUtils.getAppScreenWidth() - SizeUtils.dp2px(20f)) / 3
        recommendPlaylistAdapter.register(PlaylistItemBinder(itemWidth, true, object :
            PlaylistItemBinder.OnItemClickListener {
            override fun onItemClick(item: PlaylistData) {
                CRouter.with(requireActivity())
                    .url(RoutePath.PLAYLIST_DETAIL)
                    .extra("id", item.id)
                    .start()
            }

            override fun onPlayClick(item: PlaylistData) {
                playPlaylist(item, 0)
            }
        }))
        viewBinding.rvRecommendPlaylist.adapter = recommendPlaylistAdapter
        viewBinding.rvRecommendPlaylist.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        viewBinding.rvRecommendPlaylist.addItemDecoration(
            SpacingDecoration(SizeUtils.dp2px(10f))
        )

        val updateVisibility = {
            if (userService.isLogin() && viewModel.recommendPlaylist.value.isNotEmpty()) {
                viewBinding.tvRecommendPlaylist.isVisible = true
                viewBinding.rvRecommendPlaylist.isVisible = true
            } else {
                viewBinding.tvRecommendPlaylist.isVisible = false
                viewBinding.rvRecommendPlaylist.isVisible = false
            }
        }

        lifecycleScope.launch {
            userService.profile.collectLatest {
                updateVisibility()
            }
        }

        lifecycleScope.launch {
            viewModel.recommendPlaylist.collectLatest { recommendPlaylist ->
                updateVisibility()
                recommendPlaylistAdapter.refresh(recommendPlaylist)
            }
        }
    }

    private fun initRankingList() {
        viewBinding.tvRankingList.setOnClickListener {
            CRouter.with(requireActivity())
                .url(RoutePath.RANKING)
                .start()
        }
        rankingListAdapter.register(DiscoverRankingItemBinder(object :
            DiscoverRankingItemBinder.OnItemClickListener {
            override fun onItemClick(item: PlaylistData, position: Int) {
                CRouter.with(requireActivity())
                    .url(RoutePath.PLAYLIST_DETAIL)
                    .extra("id", item.id)
                    .start()
            }

            override fun onSongClick(item: PlaylistData, songPosition: Int) {
                playPlaylist(item, songPosition)
            }
        }))
        viewBinding.vpRankingList.apply {
            val recyclerView = getChildAt(0) as RecyclerView
            recyclerView.apply {
                setPadding(SizeUtils.dp2px(16f), 0, SizeUtils.dp2px(16f), 0)
                clipToPadding = false
            }
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
            adapter = rankingListAdapter
        }

        viewModel.rankingList.observe(this) { rankingList ->
            rankingList ?: return@observe
            if (viewModel.rankingList.value?.isNotEmpty() == true) {
                viewBinding.tvRankingList.isVisible = true
                viewBinding.vpRankingList.isVisible = true
            } else {
                viewBinding.tvRankingList.isVisible = false
                viewBinding.vpRankingList.isVisible = false
            }
            rankingListAdapter.refresh(rankingList)
        }
    }

    private fun checkApiDomain(isReload: Boolean) {
        if (ConfigPreferences.apiDomain.isNotEmpty()) {
            showLoadSirSuccess()
        } else {
            showLoadSirError("请先设置云音乐API域名")
            if (isReload) {
                ApiDomainDialog.checkApiDomain(requireContext())
            }
        }
    }

    private fun playPlaylist(playlistData: PlaylistData, songPosition: Int) {
        lifecycleScope.launch {
            showLoading()
            kotlin.runCatching {
                DiscoverApi.getFullPlaylistSongList(playlistData.id)
            }.onSuccess { songListData ->
                dismissLoading()
                if (songListData.code == 200 && songListData.songs.isNotEmpty()) {
                    val songs = songListData.songs.map { it.toMediaItem() }
                    playerController.replaceAll(songs, songs.getOrElse(songPosition) { songs[0] })
                }
            }.onFailure {
                dismissLoading()
            }
        }
    }
}