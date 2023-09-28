package me.wcy.music.mine.like

import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.util.SizeUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import me.wcy.common.ext.load
import me.wcy.common.ext.loadAvatar
import me.wcy.common.ext.viewBindings
import me.wcy.common.utils.StatusBarUtils
import me.wcy.music.R
import me.wcy.music.account.service.UserService
import me.wcy.music.common.BaseMusicFragment
import me.wcy.music.common.bean.SongData
import me.wcy.music.consts.RoutePath
import me.wcy.music.databinding.FragmentLikeSongListBinding
import me.wcy.music.discover.playlist.detail.item.PlaylistSongItemBinder
import me.wcy.music.service.AudioPlayer
import me.wcy.music.utils.toEntity
import me.wcy.radapter3.RAdapter
import me.wcy.router.annotation.Route
import javax.inject.Inject

/**
 * Created by wangchenyan.top on 2023/9/26.
 */
@Route(RoutePath.LIKE_SONG_LIST, needLogin = true)
@AndroidEntryPoint
class LikeSongListFragment : BaseMusicFragment() {
    private val viewBinding by viewBindings<FragmentLikeSongListBinding>()
    private val viewModel by viewModels<LikeSongListViewModel>()
    private val adapter by lazy {
        RAdapter<SongData>()
    }

    @Inject
    lateinit var userService: UserService

    @Inject
    lateinit var audioPlayer: AudioPlayer

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

        initTitle()
        initUserInfo()
        initSongList()
        loadData()
    }

    private fun loadData() {
        lifecycleScope.launch {
            showLoadSirLoading()
            val res = viewModel.loadData()
            if (res.isSuccess()) {
                if (res.getDataOrThrow().isEmpty()) {
                    showLoadSirEmpty()
                } else {
                    showLoadSirSuccess()
                }
            } else {
                showLoadSirError(res.msg)
            }
        }
    }

    private fun initTitle() {
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
    }

    private fun initUserInfo() {
        lifecycleScope.launch {
            userService.profile.collectLatest { profile ->
                profile ?: return@collectLatest
                viewBinding.ivCreatorAvatar.loadAvatar(profile.avatarUrl)
                viewBinding.tvCreatorName.text = profile.nickname
            }
        }
        lifecycleScope.launch {
            viewModel.songList.collectLatest { songList ->
                val song = songList.firstOrNull() ?: return@collectLatest
                viewBinding.ivCover.load(song.al.picUrl, SizeUtils.dp2px(6f))
            }
        }
    }

    private fun initSongList() {
        viewBinding.llPlayAll.setOnClickListener {
            val songList = viewModel.songList.value.map { it.toEntity() }
            if (songList.isNotEmpty()) {
                audioPlayer.replaceAll(songList, songList.first())
            }
        }

        adapter.register(PlaylistSongItemBinder { item, position ->
            val songList = viewModel.songList.value.map { it.toEntity() }
            if (songList.isNotEmpty()) {
                audioPlayer.replaceAll(songList, songList[position])
            }
        })
        viewBinding.recyclerView.adapter = adapter

        lifecycleScope.launch {
            viewModel.songList.collectLatest { songList ->
                adapter.refresh(songList)
            }
        }
    }

    override fun getNavigationBarColor(): Int {
        return R.color.play_bar_bg
    }
}