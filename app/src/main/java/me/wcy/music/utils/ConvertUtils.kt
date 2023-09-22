package me.wcy.music.utils

import me.wcy.common.ext.divide
import me.wcy.common.ext.format
import java.math.RoundingMode

/**
 * Created by wangchenyan.top on 2023/9/21.
 */
object ConvertUtils {

    fun formatByWan(num: Int, dot: Int = 0): String {
        return if (num < 100000) {
            num.toString()
        } else {
            val wan = num.toDouble().divide(10000.0).format(dot, RoundingMode.HALF_DOWN)
            wan + "万"
        }
    }
}