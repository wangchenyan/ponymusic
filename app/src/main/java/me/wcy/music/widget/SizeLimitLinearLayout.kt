package me.wcy.music.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import me.wcy.music.R

/**
 * Created by wangchenyan.top on 2023/4/18.
 */
class SizeLimitLinearLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    private var maxWidth: Int = 0
    private var maxHeight: Int = 0

    init {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.SizeLimitLinearLayout)
        maxWidth = ta.getDimensionPixelSize(R.styleable.SizeLimitLinearLayout_maxWidth, 0)
        maxHeight = ta.getDimensionPixelSize(R.styleable.SizeLimitLinearLayout_maxHeight, 0)
        ta.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var widthSpec = widthMeasureSpec
        var heightSpec = heightMeasureSpec
        if (maxWidth > 0 && MeasureSpec.getSize(widthSpec) > maxWidth) {
            widthSpec = MeasureSpec.makeMeasureSpec(maxWidth, MeasureSpec.AT_MOST)
        }
        if (maxHeight > 0 && MeasureSpec.getSize(heightSpec) > maxHeight) {
            heightSpec = MeasureSpec.makeMeasureSpec(maxHeight, MeasureSpec.AT_MOST)
        }
        super.onMeasure(widthSpec, heightSpec)
    }

    /**
     * 设置最大宽度，单位为px
     */
    fun setMaxWidth(value: Int) {
        this.maxWidth = value
        requestLayout()
    }

    /**
     * 设置最大高度，单位为px
     */
    fun setMaxHeight(value: Int) {
        this.maxHeight = value
        requestLayout()
    }
}