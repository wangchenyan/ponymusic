package me.wcy.music.discover.ranking.item

import android.view.LayoutInflater
import androidx.core.text.buildSpannedString
import androidx.core.view.get
import androidx.core.view.isEmpty
import androidx.core.view.isVisible
import com.blankj.utilcode.util.SizeUtils
import top.wangchenyan.common.ext.context
import top.wangchenyan.common.widget.CustomSpan.appendStyle
import me.wcy.music.common.bean.PlaylistData
import me.wcy.music.databinding.ItemOfficialRankingBinding
import me.wcy.music.databinding.ItemOfficialRankingSongBinding
import me.wcy.music.utils.ImageUtils.loadCover
import me.wcy.music.utils.getSimpleArtist
import me.wcy.radapter3.RItemBinder
import kotlin.reflect.KClass

/**
 * Created by wangchenyan.top on 2023/10/24.
 */
class OfficialRankingItemBinder(private val listener: OnItemClickListener) :
    RItemBinder<ItemOfficialRankingBinding, PlaylistData>() {
    override fun onBind(
        viewBinding: ItemOfficialRankingBinding,
        item: PlaylistData,
        position: Int
    ) {
        viewBinding.root.setOnClickListener {
            listener.onItemClick(item, position)
        }
        viewBinding.ivPlay.setOnClickListener {
            listener.onPlayClick(item, position)
        }
        viewBinding.tvName.text = item.name
        viewBinding.tvUpdateTime.text = item.updateFrequency
        viewBinding.ivCover.loadCover(item.getSmallCover(), SizeUtils.dp2px(6f))
        if (viewBinding.llSongContainer.isEmpty()) {
            for (i in 0 until 3) {
                ItemOfficialRankingSongBinding.inflate(
                    LayoutInflater.from(viewBinding.context),
                    viewBinding.llSongContainer,
                    true
                )
            }
        }
        for (i in 0 until 3) {
            val songItem = item.songList.getOrNull(i)
            val itemBinding = ItemOfficialRankingSongBinding.bind(viewBinding.llSongContainer[i])
            if (songItem == null) {
                itemBinding.root.isVisible = false
            } else {
                itemBinding.root.isVisible = true
                itemBinding.tvIndex.text = (i + 1).toString()
                itemBinding.tvTitle.text = buildSpannedString {
                    appendStyle(songItem.name, isBold = true)
                    append(" - ")
                    append(songItem.getSimpleArtist())
                }
            }
        }
    }

    override fun getViewBindingClazz(): KClass<*> {
        return ItemOfficialRankingBinding::class
    }

    interface OnItemClickListener {
        fun onItemClick(item: PlaylistData, position: Int)
        fun onPlayClick(item: PlaylistData, position: Int)
    }
}