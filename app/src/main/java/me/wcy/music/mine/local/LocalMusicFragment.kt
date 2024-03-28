package me.wcy.music.mine.local

import android.view.View
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.util.ConvertUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.wcy.music.R
import me.wcy.music.common.BaseMusicFragment
import me.wcy.music.common.OnItemClickListener2
import me.wcy.music.common.dialog.songmenu.SimpleMenuItem
import me.wcy.music.common.dialog.songmenu.SongMoreMenuDialog
import me.wcy.music.consts.RoutePath
import me.wcy.music.databinding.FragmentLocalMusicBinding
import me.wcy.music.service.PlayerController
import me.wcy.music.storage.db.entity.SongEntity
import me.wcy.music.utils.TimeUtils
import me.wcy.music.utils.toMediaItem
import me.wcy.radapter3.RAdapter
import me.wcy.router.CRouter
import me.wcy.router.annotation.Route
import top.wangchenyan.common.ext.viewBindings
import top.wangchenyan.common.permission.Permissioner
import javax.inject.Inject

/**
 * Created by wangchenyan.top on 2023/8/30.
 */
@Route(RoutePath.LOCAL_SONG)
@AndroidEntryPoint
class LocalMusicFragment : BaseMusicFragment() {
    private val viewBinding by viewBindings<FragmentLocalMusicBinding>()
    private val localMusicLoader by lazy {
        LocalMusicLoader()
    }
    private val adapter by lazy {
        RAdapter<SongEntity>()
    }

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
        loadData()
    }

    override fun onLazyCreate() {
        super.onLazyCreate()

        adapter.register(LocalSongItemBinder(object : OnItemClickListener2<SongEntity> {
            override fun onItemClick(item: SongEntity, position: Int) {
                val mediaList = adapter.getDataList().map { it.toMediaItem() }
                playerController.replaceAll(mediaList, mediaList[position])
                CRouter.with(requireContext()).url(RoutePath.PLAYING).start()
            }

            override fun onMoreClick(item: SongEntity, position: Int) {
                SongMoreMenuDialog(requireActivity(), item)
                    .setItems(
                        listOf(
                            SimpleMenuItem("文件名称: ${item.fileName}"),
                            SimpleMenuItem("播放时长: ${TimeUtils.formatMs(item.duration)}"),
                            SimpleMenuItem(
                                "文件大小: ${ConvertUtils.byte2FitMemorySize(item.fileSize)}"
                            ),
                            SimpleMenuItem("文件路径: ${item.path}")
                        )
                    )
                    .show()
            }
        }))
        viewBinding.recyclerView.adapter = adapter

        viewBinding.tvPlayAll.setOnClickListener {
            val mediaList = adapter.getDataList().map { it.toMediaItem() }
            playerController.replaceAll(mediaList, mediaList.first())
            CRouter.with(requireContext()).url(RoutePath.PLAYING).start()
        }

        loadData()
    }

    private fun loadData() {
        showLoadSirLoading()
        Permissioner.requestStoragePermission(requireContext()) { granted, shouldRationale ->
            if (granted) {
                lifecycleScope.launch {
                    val songList = withContext(Dispatchers.Default) {
                        localMusicLoader.load(requireContext())
                    }
                    if (songList.isNotEmpty()) {
                        showLoadSirSuccess()
                        viewBinding.tvPlayAll.text = "播放全部(${songList.size})"
                        adapter.refresh(songList)
                    } else {
                        showLoadSirEmpty(getString(R.string.no_local_music))
                    }
                }
            } else {
                showLoadSirError(getString(R.string.no_permission_storage))
            }
        }
    }

    override fun getNavigationBarColor(): Int {
        return R.color.play_bar_bg
    }
}