package me.wcy.music.discover.ranking.item

import android.view.Gravity
import android.widget.FrameLayout
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
        viewBinding.content.setOnClickListener {
            listener.onItemClick(item, position)
        }
        viewBinding.ivPlay.setOnClickListener {
            listener.onPlayClick(item, position)
        }
        val selectedPosition = position - listener.getFirstSelectedPosition()
        val gravity = when (selectedPosition % 3) {
            0 -> Gravity.START
            1 -> Gravity.CENTER_HORIZONTAL
            2 -> Gravity.END
            else -> Gravity.START
        }
        val lp = viewBinding.content.layoutParams as FrameLayout.LayoutParams
        if (lp.width != itemWidth || lp.height != itemWidth || lp.gravity != gravity) {
            lp.width = itemWidth
            lp.height = itemWidth
            lp.gravity = gravity
            viewBinding.content.layoutParams = lp
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