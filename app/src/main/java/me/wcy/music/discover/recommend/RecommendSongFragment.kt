package me.wcy.music.discover.recommend

import android.view.View
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import me.wcy.common.ext.viewBindings
import me.wcy.common.net.apiCall
import me.wcy.music.R
import me.wcy.music.common.BaseMusicFragment
import me.wcy.music.common.bean.SongData
import me.wcy.music.consts.RoutePath
import me.wcy.music.databinding.FragmentRecommendSongBinding
import me.wcy.music.discover.DiscoverApi
import me.wcy.music.discover.recommend.item.OnlineSongItemBinder
import me.wcy.music.service.AudioPlayer
import me.wcy.music.utils.toEntity
import me.wcy.radapter3.RAdapter
import me.wcy.router.annotation.Route
import javax.inject.Inject

/**
 * Created by wangchenyan.top on 2023/9/15.
 */
@Route(RoutePath.RECOMMEND_SONG, needLogin = true)
@AndroidEntryPoint
class RecommendSongFragment : BaseMusicFragment() {
    private val viewBinding by viewBindings<FragmentRecommendSongBinding>()
    private val adapter by lazy {
        RAdapter<SongData>()
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

        adapter.register(OnlineSongItemBinder { item, position ->
            val entityList = adapter.getDataList().map { it.toEntity() }
            audioPlayer.replaceAll(entityList, entityList[position])
        })
        viewBinding.recyclerView.adapter = adapter
        viewBinding.tvPlayAll.setOnClickListener {
            val entityList = adapter.getDataList().map { it.toEntity() }
            audioPlayer.replaceAll(entityList, entityList.first())
        }

        loadData()
    }

    private fun loadData() {
        lifecycleScope.launch {
            showLoadSirLoading()
            val res = apiCall { DiscoverApi.get().getRecommendSongs() }
            if (res.isSuccessWithData()) {
                showLoadSirSuccess()
                adapter.refresh(res.getDataOrThrow().dailySongs)
            } else {
                showLoadSirError(res.msg)
            }
        }
    }

    override fun getNavigationBarColor(): Int {
        return R.color.play_bar_bg
    }
}