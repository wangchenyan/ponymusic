package me.wcy.music.discover.recommend.item

import androidx.core.view.isVisible
import com.blankj.utilcode.util.SizeUtils
import me.wcy.common.ext.load
import me.wcy.music.common.bean.SongData
import me.wcy.music.databinding.ItemOnlineSongBinding
import me.wcy.music.utils.getSimpleArtist
import me.wcy.radapter3.RItemBinder

/**
 * Created by wangchenyan.top on 2023/9/15.
 */
class OnlineSongItemBinder(private val onItemClick: (SongData, Int) -> Unit) :
    RItemBinder<ItemOnlineSongBinding, SongData>() {

    override fun onBind(viewBinding: ItemOnlineSongBinding, item: SongData, position: Int) {
        viewBinding.root.setOnClickListener {
            onItemClick(item, position)
        }
        viewBinding.ivCover.load(item.al.picUrl, SizeUtils.dp2px(4f))
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