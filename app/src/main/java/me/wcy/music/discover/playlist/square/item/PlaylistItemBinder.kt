package me.wcy.music.discover.playlist.square.item

import androidx.core.view.isVisible
import com.blankj.utilcode.util.SizeUtils
import me.wcy.music.common.bean.PlaylistData
import me.wcy.music.databinding.ItemDiscoverPlaylistBinding
import me.wcy.music.utils.ConvertUtils
import me.wcy.music.utils.ImageUtils.loadCover
import me.wcy.radapter3.RItemBinder

/**
 * Created by wangchenyan.top on 2023/9/25.
 */
class PlaylistItemBinder(
    private val itemWidth: Int,
    private val showPlayButton: Boolean,
    private val listener: OnItemClickListener
) : RItemBinder<ItemDiscoverPlaylistBinding, PlaylistData>() {

    override fun onBind(
        viewBinding: ItemDiscoverPlaylistBinding,
        item: PlaylistData,
        position: Int
    ) {
        viewBinding.root.setOnClickListener {
            listener.onItemClick(item)
        }
        viewBinding.ivPlay.isVisible = showPlayButton
        viewBinding.ivPlay.setOnClickListener {
            listener.onPlayClick(item)
        }
        val lp = viewBinding.ivCover.layoutParams
        lp.width = itemWidth
        lp.height = itemWidth
        viewBinding.ivCover.layoutParams = lp
        viewBinding.ivCover.loadCover(item.getSmallCover(), SizeUtils.dp2px(6f))
        viewBinding.tvPlayCount.text = ConvertUtils.formatPlayCount(item.playCount)
        viewBinding.tvName.text = item.name
    }

    interface OnItemClickListener {
        fun onItemClick(item: PlaylistData)
        fun onPlayClick(item: PlaylistData)
    }
}