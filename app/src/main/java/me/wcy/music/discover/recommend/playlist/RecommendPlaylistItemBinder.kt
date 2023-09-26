package me.wcy.music.discover.recommend.playlist

import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.SizeUtils
import me.wcy.music.common.bean.PlaylistData
import me.wcy.music.databinding.ItemRecommendPlaylistBinding
import me.wcy.music.utils.ConvertUtils
import me.wcy.music.utils.ImageUtils.loadCover
import me.wcy.radapter3.RItemBinder

/**
 * Created by wangchenyan.top on 2023/9/25.
 */
class RecommendPlaylistItemBinder(private val listener: OnClickListener) :
    RItemBinder<ItemRecommendPlaylistBinding, PlaylistData>() {

    override fun onBind(
        viewBinding: ItemRecommendPlaylistBinding,
        item: PlaylistData,
        position: Int
    ) {
        viewBinding.root.setOnClickListener {
            listener.onItemClick(item)
        }
        viewBinding.ivPlay.setOnClickListener {
            listener.onPlayClick(item)
        }
        val size = (ScreenUtils.getAppScreenWidth() - SizeUtils.dp2px(20f)) / 3
        val lp = viewBinding.ivCover.layoutParams
        lp.width = size
        lp.height = size
        viewBinding.ivCover.layoutParams = lp
        viewBinding.ivCover.loadCover(item.coverImgUrl, SizeUtils.dp2px(6f))
        viewBinding.tvPlayCount.text = ConvertUtils.formatPlayCount(item.playCount)
        viewBinding.tvName.text = item.name
    }

    interface OnClickListener {
        fun onItemClick(item: PlaylistData)
        fun onPlayClick(item: PlaylistData)
    }
}