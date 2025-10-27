package me.wcy.music.discover.ranking.item

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by wangchenyan.top on 2025/10/27.
 */
class SelectedRankingSpacingDecoration(
    private val horizontalSpacing: Int,
    private val verticalSpacing: Int,
    private val getFirstSelectedPosition: () -> Int
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val layoutManager = parent.layoutManager
        val firstSelectedPosition = getFirstSelectedPosition()
        val position = parent.getChildAdapterPosition(view)
        val realPosition = position - firstSelectedPosition
        if (layoutManager is GridLayoutManager && realPosition >= 0) {
            val spanCount = layoutManager.spanCount
            val column = realPosition % spanCount
            // column * ((1f / spanCount) * spacing)
            outRect.left = column * horizontalSpacing / spanCount
            // spacing - (column + 1) * ((1f / spanCount) * spacing)
            outRect.right = horizontalSpacing - (column + 1) * horizontalSpacing / spanCount
            outRect.top = verticalSpacing
        }
    }
}