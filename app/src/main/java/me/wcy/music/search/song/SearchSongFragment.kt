package me.wcy.music.search.song

import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import me.wcy.common.model.CommonResult
import me.wcy.common.net.apiCall
import me.wcy.music.common.OnItemClickListener2
import me.wcy.music.common.SimpleMusicRefreshFragment
import me.wcy.music.common.bean.SongData
import me.wcy.music.common.dialog.songmenu.SongMoreMenuDialog
import me.wcy.music.common.dialog.songmenu.items.AlbumMenuItem
import me.wcy.music.common.dialog.songmenu.items.ArtistMenuItem
import me.wcy.music.common.dialog.songmenu.items.CollectMenuItem
import me.wcy.music.common.dialog.songmenu.items.CommentMenuItem
import me.wcy.music.consts.Consts
import me.wcy.music.search.SearchApi
import me.wcy.music.search.SearchViewModel
import me.wcy.music.service.AudioPlayer
import me.wcy.music.utils.toEntity
import me.wcy.radapter3.RAdapter
import javax.inject.Inject

/**
 * Created by wangchenyan.top on 2023/9/20.
 */
@AndroidEntryPoint
class SearchSongFragment : SimpleMusicRefreshFragment<SongData>() {
    private val viewModel by activityViewModels<SearchViewModel>()
    private val itemBinder by lazy {
        SearchSongItemBinder(object : OnItemClickListener2<SongData> {
            override fun onItemClick(item: SongData, position: Int) {
                audioPlayer.addAndPlay(item.toEntity())
            }

            override fun onMoreClick(item: SongData, position: Int) {
                SongMoreMenuDialog(requireActivity(), item)
                    .setItems(
                        listOf(
                            CollectMenuItem(lifecycleScope, item),
                            CommentMenuItem(item),
                            ArtistMenuItem(item),
                            AlbumMenuItem(item)
                        )
                    )
                    .show()
            }
        }).apply {
            keywords = viewModel.keywords.value
        }
    }

    @Inject
    lateinit var audioPlayer: AudioPlayer

    override fun isShowTitle(): Boolean {
        return false
    }

    override fun isRefreshEnabled(): Boolean {
        return false
    }

    override fun onLazyCreate() {
        super.onLazyCreate()
        lifecycleScope.launch {
            viewModel.keywords.collectLatest {
                if (it.isNotEmpty()) {
                    showLoadSirLoading()
                    itemBinder.keywords = it
                    autoRefresh(true)
                }
            }
        }
    }

    override fun initAdapter(adapter: RAdapter<SongData>) {
        adapter.register(itemBinder)
    }

    override suspend fun getData(page: Int): CommonResult<List<SongData>> {
        val keywords = viewModel.keywords.value
        if (keywords.isEmpty()) {
            return CommonResult.success(emptyList())
        }
        val res = apiCall {
            SearchApi.get().search(1, keywords, Consts.PAGE_COUNT, (page - 1) * Consts.PAGE_COUNT)
        }
        return if (res.isSuccessWithData()) {
            CommonResult.success(res.getDataOrThrow().songs)
        } else {
            CommonResult.fail(res.code, res.msg)
        }
    }
}