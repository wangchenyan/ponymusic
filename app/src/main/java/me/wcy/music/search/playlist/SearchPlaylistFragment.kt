package me.wcy.music.search.playlist

import dagger.hilt.android.AndroidEntryPoint
import me.wcy.common.model.CommonResult
import me.wcy.music.common.SimpleMusicRefreshFragment
import me.wcy.music.common.bean.PlaylistData
import me.wcy.radapter3.RAdapter

/**
 * Created by wangchenyan.top on 2023/9/20.
 */
@AndroidEntryPoint
class SearchPlaylistFragment : SimpleMusicRefreshFragment<PlaylistData>() {

    override fun isShowTitle(): Boolean {
        return false
    }

    override fun initAdapter(adapter: RAdapter<PlaylistData>) {
    }

    override suspend fun getData(page: Int): CommonResult<List<PlaylistData>> {
        return CommonResult.success(emptyList())
    }
}