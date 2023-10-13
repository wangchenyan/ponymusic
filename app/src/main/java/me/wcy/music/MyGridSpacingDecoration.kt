package me.wcy.music

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by wangchenyan.top on 2023/10/18.
 */
class MyGridSpacingDecoration(
    private val horizontalSpacing: Int,
    private val verticalSpacing: Int,
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val layoutManager = parent.layoutManager
        if (layoutManager is GridLayoutManager) {
            val spanCount = layoutManager.spanCount
            val position = parent.getChildAdapterPosition(view)
            val column = position % spanCount
            // column * ((1f / spanCount) * spacing)
            outRect.left = column * horizontalSpacing / spanCount
            // spacing - (column + 1) * ((1f / spanCount) * spacing)
            outRect.right = horizontalSpacing - (column + 1) * horizontalSpacing / spanCount
            if (position >= spanCount) {
                outRect.top = verticalSpacing
            }
        }
    }
}