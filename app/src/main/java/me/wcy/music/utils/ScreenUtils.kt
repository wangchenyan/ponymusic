package me.wcy.music.utils

import android.content.Context
import android.view.WindowManager

/**
 * 工具类
 * Created by hzwangchenyan on 2016/1/6.
 */
object ScreenUtils {
    private var sContext: Context? = null
    fun init(context: Context?) {
        sContext = context!!.applicationContext
    }

    val screenWidth: Int
        get() {
            val wm = sContext!!.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            return wm.defaultDisplay.width
        }
    val statusBarHeight: Int
        /**
         * 获取状态栏高度
         */
        get() {
            var result = 0
            val resourceId =
                sContext!!.resources.getIdentifier("status_bar_height", "dimen", "android")
            if (resourceId > 0) {
                result = sContext!!.resources.getDimensionPixelSize(resourceId)
            }
            return result
        }

    fun dp2px(dpValue: Float): Int {
        val scale = sContext!!.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    fun px2dp(pxValue: Float): Int {
        val scale = sContext!!.resources.displayMetrics.density
        return (pxValue / scale + 0.5f).toInt()
    }

    fun sp2px(spValue: Float): Int {
        val fontScale = sContext!!.resources.displayMetrics.scaledDensity
        return (spValue * fontScale + 0.5f).toInt()
    }
}