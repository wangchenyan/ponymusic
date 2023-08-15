package me.wcy.music.widget

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import me.wcy.music.R
import me.wcy.music.utils.ScreenUtils

/**
 * 播放页Indicator
 * Created by wcy on 2015/11/30.
 */
class IndicatorLayout @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    init {
        init()
    }

    private fun init() {
        orientation = HORIZONTAL
        gravity = Gravity.CENTER
    }

    fun create(count: Int) {
        for (i in 0 until count) {
            val imageView = ImageView(context)
            imageView.layoutParams = LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
            )
            val padding = ScreenUtils.dp2px(3f)
            imageView.setPadding(padding, 0, padding, 0)
            imageView.setImageResource(if (i == 0) R.drawable.ic_play_page_indicator_selected else R.drawable.ic_play_page_indicator_unselected)
            addView(imageView)
        }
    }

    fun setCurrent(position: Int) {
        val count = childCount
        for (i in 0 until count) {
            val imageView = getChildAt(i) as ImageView
            if (i == position) {
                imageView.setImageResource(R.drawable.ic_play_page_indicator_selected)
            } else {
                imageView.setImageResource(R.drawable.ic_play_page_indicator_unselected)
            }
        }
    }
}