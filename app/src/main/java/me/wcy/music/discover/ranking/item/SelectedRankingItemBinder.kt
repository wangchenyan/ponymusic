package me.wcy.music.discover.ranking.item

import android.view.ViewGroup.MarginLayoutParams
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
    private val itemWidth: Int,
    private val listener: OnItemClickListener
) : RItemBinder<ItemSelectedRankingBinding, PlaylistData>() {
    override fun onBind(
        viewBinding: ItemSelectedRankingBinding,
        item: PlaylistData,
        position: Int
    ) {
        viewBinding.root.setOnClickListener {
            listener.onItemClick(item, position)
        }
        viewBinding.ivPlay.setOnClickListener {
            listener.onPlayClick(item, position)
        }
        val selectedPosition = position - listener.getFirstSelectedPosition()
        val marginEnd = if (selectedPosition % 3 < 2) SizeUtils.dp2px(10f) else 0
        val lp = viewBinding.root.layoutParams as MarginLayoutParams
        if (lp.width != itemWidth || lp.height != itemWidth) {
            lp.width = itemWidth
            lp.height = itemWidth 
            viewBinding.root.layoutParams = lp
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
        fun getFirstSelectedPosition(): Int
    }
}