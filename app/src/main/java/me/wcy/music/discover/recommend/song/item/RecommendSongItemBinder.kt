package me.wcy.music.discover.recommend.song.item

import androidx.core.view.isVisible
import com.blankj.utilcode.util.SizeUtils
import me.wcy.music.common.OnItemClickListener2
import me.wcy.music.common.bean.SongData
import me.wcy.music.databinding.ItemRecommendSongBinding
import me.wcy.music.utils.ImageUtils.loadCover
import me.wcy.music.utils.getSimpleArtist
import me.wcy.radapter3.RItemBinder

/**
 * Created by wangchenyan.top on 2023/9/15.
 */
class RecommendSongItemBinder(private val listener: OnItemClickListener2<SongData>) :
    RItemBinder<ItemRecommendSongBinding, SongData>() {

    override fun onBind(viewBinding: ItemRecommendSongBinding, item: SongData, position: Int) {
        viewBinding.root.setOnClickListener {
            listener.onItemClick(item, position)
        }
        viewBinding.ivMore.setOnClickListener {
            listener.onMoreClick(item, position)
        }
        viewBinding.ivCover.loadCover(item.al.getSmallCover(), SizeUtils.dp2px(4f))
        viewBinding.tvTitle.text = item.name
        viewBinding.tvTag.isVisible = item.recommendReason.isNotEmpty()
        viewBinding.tvTag.text = item.recommendReason
        viewBinding.tvSubTitle.text = buildString {
            append(item.getSimpleArtist())
            append(" - ")
            append(item.al.name)
            item.originSongSimpleData?.let { originSong ->
                append(" | 原唱: ")
                append(originSong.artists.joinToString("/") { it.name })
            }
        }
    }
}