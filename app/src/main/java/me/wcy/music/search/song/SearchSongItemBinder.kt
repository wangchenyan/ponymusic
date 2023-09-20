package me.wcy.music.search.song

import androidx.core.view.isVisible
import me.wcy.music.common.bean.SongData
import me.wcy.music.databinding.ItemSearchSongBinding
import me.wcy.music.utils.getSimpleArtist
import me.wcy.radapter3.RItemBinder

/**
 * Created by wangchenyan.top on 2023/9/20.
 */
class SearchSongItemBinder(private val onItemClick: (SongData, Int) -> Unit) :
    RItemBinder<ItemSearchSongBinding, SongData>() {

    override fun onBind(viewBinding: ItemSearchSongBinding, item: SongData, position: Int) {
        viewBinding.root.setOnClickListener {
            onItemClick(item, position)
        }
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