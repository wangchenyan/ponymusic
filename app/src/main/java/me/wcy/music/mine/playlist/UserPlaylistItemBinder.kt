package me.wcy.music.mine.playlist

import androidx.core.view.isVisible
import com.blankj.utilcode.util.SizeUtils
import me.wcy.music.common.bean.PlaylistData
import me.wcy.music.databinding.ItemUserPlaylistBinding
import me.wcy.music.utils.ImageUtils.loadCover
import me.wcy.radapter3.RItemBinder

/**
 * Created by wangchenyan.top on 2023/9/28.
 */
class UserPlaylistItemBinder(
    private val isMine: Boolean,
    private val listener: OnItemClickListener
) : RItemBinder<ItemUserPlaylistBinding, PlaylistData>() {

    override fun onBind(viewBinding: ItemUserPlaylistBinding, item: PlaylistData, position: Int) {
        viewBinding.root.setOnClickListener {
            listener.onItemClick(item)
        }
        viewBinding.ivCover.loadCover(item.getSmallCover(), SizeUtils.dp2px(4f))
        viewBinding.tvName.text = item.name
        viewBinding.tvCount.text = if (isMine) {
            "${item.trackCount}首"
        } else {
            "${item.trackCount}首, by ${item.creator.nickname}"
        }
        viewBinding.ivMore.isVisible = isMine.not()
        viewBinding.ivMore.setOnClickListener {
            listener.onMoreClick(item)
        }
    }

    interface OnItemClickListener {
        fun onItemClick(item: PlaylistData)
        fun onMoreClick(item: PlaylistData)
    }
}