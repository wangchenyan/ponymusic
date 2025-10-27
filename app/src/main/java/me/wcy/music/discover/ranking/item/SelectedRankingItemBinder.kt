package me.wcy.music.discover.ranking.item

import androidx.core.view.updateLayoutParams
import com.blankj.utilcode.util.SizeUtils
import me.wcy.music.common.bean.PlaylistData
import me.wcy.music.databinding.ItemSelectedRankingBinding
import me.wcy.music.utils.ImageUtils.loadCover
import me.wcy.radapter3.RItemBinder
import kotlin.reflect.KClass

/**
 * Created by wangchenyan.top on 2023/10/24.
 */
class SelectedRankingItemBinder(
    private val itemHeight: Int,
    private val listener: OnItemClickListener
) : RItemBinder<ItemSelectedRankingBinding, PlaylistData>() {
    override fun onBind(
        viewBinding: ItemSelectedRankingBinding,
        item: PlaylistData,
        position: Int
    ) {
        viewBinding.root.updateLayoutParams {
            this.height = itemHeight
        }
        viewBinding.root.setOnClickListener {
            listener.onItemClick(item, position)
        }
        viewBinding.ivPlay.setOnClickListener {
            listener.onPlayClick(item, position)
        }
        viewBinding.tvName.text = item.name
        viewBinding.tvUpdateTime.text = item.updateFrequency
        viewBinding.ivCover.loadCover(item.getSmallCover(), SizeUtils.dp2px(6f))
    }

    override fun getViewBindingClazz(): KClass<*> {
        return ItemSelectedRankingBinding::class
    }

    interface OnItemClickListener {
        fun onItemClick(item: PlaylistData, position: Int)
        fun onPlayClick(item: PlaylistData, position: Int)
    }
}