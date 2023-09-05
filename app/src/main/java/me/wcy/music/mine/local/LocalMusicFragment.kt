package me.wcy.music.mine.local

import android.view.View
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.wcy.common.ext.viewBindings
import me.wcy.common.permission.Permissioner
import me.wcy.music.R
import me.wcy.music.common.BaseMusicFragment
import me.wcy.music.databinding.FragmentLocalMusicBinding
import me.wcy.music.service.AudioPlayer
import me.wcy.music.storage.db.entity.SongEntity
import me.wcy.radapter3.RAdapter
import me.wcy.router.annotation.Route
import javax.inject.Inject

/**
 * Created by wangchenyan.top on 2023/8/30.
 */
@Route("/local_music")
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
    lateinit var audioPlayer: AudioPlayer

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

        adapter.register(LocalSongItemBinder {
            audioPlayer.replaceAll(adapter.getDataList(), it)
        })
        viewBinding.recyclerView.adapter = adapter

        viewBinding.tvPlayAll.setOnClickListener {
            audioPlayer.replaceAll(adapter.getDataList(), adapter.getDataList().first())
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
}