package me.wcy.music.discover.playlist.square

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.SizeUtils
import dagger.hilt.android.AndroidEntryPoint
import me.wcy.music.R
import me.wcy.music.common.SimpleMusicRefreshFragment
import me.wcy.music.common.bean.PlaylistData
import me.wcy.music.consts.Consts
import me.wcy.music.consts.RoutePath
import me.wcy.music.discover.DiscoverApi
import me.wcy.music.discover.playlist.square.item.PlaylistItemBinder
import me.wcy.radapter3.RAdapter
import me.wcy.router.CRouter
import top.wangchenyan.common.model.CommonResult
import top.wangchenyan.common.widget.decoration.GridSpacingDecoration

/**
 * Created by wangchenyan.top on 2023/9/26.
 */
@AndroidEntryPoint
class PlaylistTabFragment : SimpleMusicRefreshFragment<PlaylistData>() {
    private val cat by lazy {
        getRouteArguments().getStringExtra("tag") ?: ""
    }
    private val spanCount: Int by lazy {
        val containerWidth = ScreenUtils.getAppScreenWidth() - SizeUtils.dp2px(32f)
        val itemWidth = resources.getDimensionPixelSize(R.dimen.playlist_item_max_width)
        val itemSpace = SizeUtils.dp2px(10f)
        val count = containerWidth / (itemWidth + itemSpace)
        count.coerceAtLeast(3)
    }
    private val itemWidth: Int by lazy {
        val containerWidth = ScreenUtils.getAppScreenWidth() - SizeUtils.dp2px(32f)
        val itemSpace = SizeUtils.dp2px(10f)
        (containerWidth - itemSpace * (spanCount - 1)) / spanCount
    }

    override fun isShowTitle(): Boolean {
        return false
    }

    override fun isRefreshEnabled(): Boolean {
        return false
    }

    override fun getLayoutManager(): RecyclerView.LayoutManager {
        return GridLayoutManager(requireContext(), spanCount)
    }

    override fun initAdapter(adapter: RAdapter<PlaylistData>) {
        adapter.register(
            PlaylistItemBinder(
                itemWidth,
                false,
                object : PlaylistItemBinder.OnItemClickListener {
                    override fun onItemClick(item: PlaylistData) {
                        CRouter.with(requireActivity())
                            .url(RoutePath.PLAYLIST_DETAIL)
                            .extra("id", item.id)
                            .start()
                    }

                    override fun onPlayClick(item: PlaylistData) {
                    }
                })
        )
    }

    override fun onLazyCreate() {
        super.onLazyCreate()
        getRecyclerView().apply {
            val paddingHorizontal = SizeUtils.dp2px(16f)
            val paddingVertical = SizeUtils.dp2px(10f)
            setPadding(paddingHorizontal, paddingVertical, paddingHorizontal, paddingVertical)
            clipToPadding = false
            val spacing = SizeUtils.dp2px(10f)
            addItemDecoration(GridSpacingDecoration(spacing, spacing))
        }
    }

    override suspend fun getData(page: Int): CommonResult<List<PlaylistData>> {
        val res = kotlin.runCatching {
            DiscoverApi.get()
                .getPlaylistList(
                    cat,
                    Consts.PAGE_COUNT_GRID,
                    (page - 1) * Consts.PAGE_COUNT_GRID
                )
        }
        return if (res.getOrNull()?.code == 200) {
            CommonResult.success(res.getOrThrow().playlists)
        } else {
            CommonResult.fail(msg = res.exceptionOrNull()?.message)
        }
    }
}