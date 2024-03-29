package me.wcy.music.discover.playlist.detail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.util.SizeUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import me.wcy.music.R
import me.wcy.music.account.service.UserService
import me.wcy.music.common.BaseMusicFragment
import me.wcy.music.common.OnItemClickListener2
import me.wcy.music.common.bean.SongData
import me.wcy.music.common.dialog.songmenu.SongMoreMenuDialog
import me.wcy.music.common.dialog.songmenu.items.AlbumMenuItem
import me.wcy.music.common.dialog.songmenu.items.ArtistMenuItem
import me.wcy.music.common.dialog.songmenu.items.CollectMenuItem
import me.wcy.music.common.dialog.songmenu.items.CommentMenuItem
import me.wcy.music.common.dialog.songmenu.items.DeletePlaylistSongMenuItem
import me.wcy.music.consts.RoutePath
import me.wcy.music.databinding.FragmentPlaylistDetailBinding
import me.wcy.music.databinding.ItemPlaylistTagBinding
import me.wcy.music.discover.playlist.detail.item.PlaylistSongItemBinder
import me.wcy.music.discover.playlist.detail.viewmodel.PlaylistViewModel
import me.wcy.music.service.PlayerController
import me.wcy.music.utils.ConvertUtils
import me.wcy.music.utils.ImageUtils.loadCover
import me.wcy.music.utils.toMediaItem
import me.wcy.radapter3.RAdapter
import me.wcy.router.CRouter
import me.wcy.router.annotation.Route
import top.wangchenyan.common.ext.loadAvatar
import top.wangchenyan.common.ext.toast
import top.wangchenyan.common.ext.viewBindings
import top.wangchenyan.common.utils.StatusBarUtils
import javax.inject.Inject

/**
 * Created by wangchenyan.top on 2023/9/22.
 */
@Route(RoutePath.PLAYLIST_DETAIL)
@AndroidEntryPoint
class PlaylistDetailFragment : BaseMusicFragment() {
    private val viewBinding by viewBindings<FragmentPlaylistDetailBinding>()
    private val viewModel by viewModels<PlaylistViewModel>()
    private val adapter by lazy { RAdapter<SongData>() }
    private var collectMenu: View? = null

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
        return viewBinding.coordinatorLayout
    }

    override fun onReload() {
        super.onReload()
        loadData()
    }

    override fun onLazyCreate() {
        super.onLazyCreate()

        val id = getRouteArguments().getLongExtra("id", 0)
        val realtimeData = getRouteArguments().getBooleanExtra("realtime_data", false)
        val isLike = getRouteArguments().getBooleanExtra("is_like", false)
        if (id <= 0) {
            finish()
            return
        }

        viewModel.init(id, realtimeData, isLike)

        initTitle()
        initPlaylistInfo()
        initSongList()
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

    private fun initTitle() {
        collectMenu = getTitleLayout()?.addImageMenu(R.drawable.ic_favorite_selector, false)
        collectMenu?.setOnClickListener {
            viewModel.playlistData.value ?: return@setOnClickListener
            userService.checkLogin(requireActivity()) {
                lifecycleScope.launch {
                    showLoading()
                    val res = viewModel.collect()
                    dismissLoading()
                    if (res.isSuccess()) {
                        toast("操作成功")
                    } else {
                        toast(res.msg)
                    }
                }
            }
        }

        StatusBarUtils.getStatusBarHeight(requireActivity()) {
            (viewBinding.titlePlaceholder.layoutParams as ViewGroup.MarginLayoutParams).apply {
                topMargin = it
                viewBinding.titlePlaceholder.requestLayout()
            }
            viewBinding.toolbarPlaceholder.layoutParams.height =
                requireContext().resources.getDimensionPixelSize(R.dimen.common_title_bar_size) + it
            viewBinding.toolbarPlaceholder.requestLayout()
        }

        viewBinding.appBarLayout.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            getTitleLayout()?.updateScroll(-verticalOffset)
        }

        lifecycleScope.launch {
            userService.profile.collectLatest { profile ->
                updateCollectState()
            }
        }
    }

    private fun initPlaylistInfo() {
        lifecycleScope.launch {
            viewModel.playlistData.collectLatest { playlistData ->
                playlistData ?: return@collectLatest
                getTitleLayout()?.setTitleText(playlistData.name)
                updateCollectState()
                viewBinding.ivCover.loadCover(playlistData.getSmallCover(), SizeUtils.dp2px(6f))
                viewBinding.tvPlayCount.text =
                    ConvertUtils.formatPlayCount(playlistData.playCount)
                viewBinding.tvName.text = playlistData.name
                viewBinding.ivCreatorAvatar.loadAvatar(playlistData.creator.avatarUrl)
                viewBinding.tvCreatorName.text = playlistData.creator.nickname
                viewBinding.tvSongCount.text = "(${playlistData.trackCount})"

                viewBinding.flTags.removeAllViews()
                playlistData.tags.forEach { tag ->
                    ItemPlaylistTagBinding.inflate(
                        LayoutInflater.from(context),
                        viewBinding.flTags,
                        true
                    ).apply {
                        root.text = tag
                    }
                }

                viewBinding.tvDesc.text = playlistData.description
            }
        }
    }

    private fun initSongList() {
        viewBinding.llPlayAll.setOnClickListener {
            val songList = viewModel.songList.value.map { it.toMediaItem() }
            if (songList.isNotEmpty()) {
                playerController.replaceAll(songList, songList.first())
                CRouter.with(requireContext()).url(RoutePath.PLAYING).start()
            }
        }

        adapter.register(PlaylistSongItemBinder(object : OnItemClickListener2<SongData> {
            override fun onItemClick(item: SongData, position: Int) {
                val songList = viewModel.songList.value.map { it.toMediaItem() }
                if (songList.isNotEmpty()) {
                    playerController.replaceAll(songList, songList[position])
                    CRouter.with(requireContext()).url(RoutePath.PLAYING).start()
                }
            }

            override fun onMoreClick(item: SongData, position: Int) {
                val items = mutableListOf(
                    CollectMenuItem(lifecycleScope, item),
                    CommentMenuItem(item),
                    ArtistMenuItem(item),
                    AlbumMenuItem(item)
                )
                val playlistData = viewModel.playlistData.value
                if (playlistData != null && playlistData.creator.userId == userService.getUserId()) {
                    items.add(DeletePlaylistSongMenuItem(playlistData, item) {
                        viewModel.removeSong(it)
                    })
                }
                SongMoreMenuDialog(requireActivity(), item)
                    .setItems(items)
                    .show()
            }
        }))
        viewBinding.recyclerView.adapter = adapter

        lifecycleScope.launch {
            viewModel.songList.collectLatest { songList ->
                adapter.refresh(songList)
            }
        }
    }

    private fun updateCollectState() {
        val playlistData = viewModel.playlistData.value
        if (playlistData == null) {
            collectMenu?.isVisible = false
            return
        }
        if (userService.getUserId() == playlistData.userId) {
            collectMenu?.isVisible = false
            return
        }
        collectMenu?.isVisible = true
        collectMenu?.isSelected = playlistData.subscribed
    }

    override fun getNavigationBarColor(): Int {
        return R.color.play_bar_bg
    }
}