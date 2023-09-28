package me.wcy.music.mine.home

import android.annotation.SuppressLint
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.util.SizeUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import me.wcy.common.ext.load
import me.wcy.common.ext.loadAvatar
import me.wcy.common.ext.viewBindings
import me.wcy.common.widget.decoration.SpacingDecoration
import me.wcy.music.R
import me.wcy.music.account.service.UserService
import me.wcy.music.common.ApiDomainDialog
import me.wcy.music.common.BaseMusicFragment
import me.wcy.music.common.bean.PlaylistData
import me.wcy.music.consts.RoutePath
import me.wcy.music.databinding.FragmentMineBinding
import me.wcy.music.main.MainActivity
import me.wcy.music.mine.home.viewmodel.MineViewModel
import me.wcy.music.mine.playlist.UserPlaylistItemBinder
import me.wcy.radapter3.RAdapter
import me.wcy.router.CRouter
import javax.inject.Inject

/**
 * Created by wangchenyan.top on 2023/8/21.
 */
@AndroidEntryPoint
class MineFragment : BaseMusicFragment() {
    private val viewBinding by viewBindings<FragmentMineBinding>()
    private val viewModel by viewModels<MineViewModel>()

    @Inject
    lateinit var userService: UserService

    override fun getRootView(): View {
        return viewBinding.root
    }

    override fun onLazyCreate() {
        super.onLazyCreate()

        initTitle()
        initProfile()
        initLocalMusic()
        initLikeSong()
        initPlaylist()
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
            addImageMenu(
                R.drawable.ic_menu_search,
                isDayNight = true,
                isLeft = false
            ).setOnClickListener {
                if (ApiDomainDialog.checkApiDomain(requireContext())) {
                    CRouter.with(requireActivity()).url(RoutePath.SEARCH).start()
                }
            }
        }
    }

    private fun initProfile() {
        lifecycleScope.launch {
            userService.profile.collectLatest { profile ->
                viewBinding.ivAvatar.loadAvatar(profile?.avatarUrl)
                viewBinding.tvNickName.text = profile?.nickname
                viewBinding.flProfile.setOnClickListener {
                    if (ApiDomainDialog.checkApiDomain(requireActivity())) {
                        if (userService.isLogin().not()) {
                            CRouter.with(requireActivity())
                                .url(RoutePath.LOGIN)
                                .start()
                        }
                    }
                }
            }
        }
    }

    private fun initLocalMusic() {
        viewBinding.localMusic.setOnClickListener {
            CRouter.with().url(RoutePath.LOCAL_SONG).start()
        }
    }

    private fun initLikeSong() {
        viewBinding.llLikeSong.setOnClickListener {
            CRouter.with(requireActivity())
                .url(RoutePath.LIKE_SONG_LIST)
                .start()
        }
        lifecycleScope.launch {
            userService.profile.collectLatest {
                viewBinding.llLikeSong.isVisible = userService.isLogin()
            }
        }
        lifecycleScope.launch {
            viewModel.likeSongData.collectLatest { likeSongData ->
                viewBinding.ivLikeSongCover.load(likeSongData.cover, SizeUtils.dp2px(4f))
                viewBinding.tvLikeSongCount.text = "${likeSongData.count}首"
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initPlaylist() {
        val listener = object : UserPlaylistItemBinder.OnItemClickListener {
            override fun onItemClick(item: PlaylistData) {
                CRouter.with(requireActivity())
                    .url(RoutePath.PLAYLIST_DETAIL)
                    .extra("id", item.id)
                    .start()
            }

            override fun onMoreClick(item: PlaylistData) {
            }
        }
        val myPlaylistAdapter = RAdapter<PlaylistData>().apply {
            register(UserPlaylistItemBinder(true, listener))
        }
        val collectPlaylistAdapter = RAdapter<PlaylistData>().apply {
            register(UserPlaylistItemBinder(false, listener))
        }

        val spacingDecoration = SpacingDecoration(SizeUtils.dp2px(10f))
        viewBinding.rvMyPlaylist.addItemDecoration(spacingDecoration)
        viewBinding.rvMyPlaylist.adapter = myPlaylistAdapter
        viewBinding.rvCollectPlaylist.addItemDecoration(spacingDecoration)
        viewBinding.rvCollectPlaylist.adapter = collectPlaylistAdapter

        lifecycleScope.launch {
            userService.profile.collectLatest {
                viewBinding.llMyPlaylist.isVisible = userService.isLogin()
                viewBinding.llCollectPlaylist.isVisible = userService.isLogin()
            }
        }

        lifecycleScope.launch {
            viewModel.myPlaylists.collectLatest { myPlaylists ->
                viewBinding.tvMyPlaylist.text = "创建歌单(${myPlaylists.size})"
                myPlaylistAdapter.refresh(myPlaylists)
            }
        }
        lifecycleScope.launch {
            viewModel.collectPlaylists.collectLatest { collectPlaylists ->
                viewBinding.tvCollectPlaylist.text = "收藏歌单(${collectPlaylists.size})"
                collectPlaylistAdapter.refresh(collectPlaylists)
            }
        }
    }
}