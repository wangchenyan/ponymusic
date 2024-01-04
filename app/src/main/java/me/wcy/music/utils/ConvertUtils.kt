package me.wcy.music.utils

import top.wangchenyan.common.ext.divide
import top.wangchenyan.common.ext.format
import java.math.RoundingMode

/**
 * Created by wangchenyan.top on 2023/9/21.
 */
object ConvertUtils {

    fun formatPlayCount(num: Long, dot: Int = 0): String {
        return if (num < 100000) {
            num.toString()
        } else if (num < 100000000) {
            val wan = num.toDouble().divide(10000.0).format(dot, RoundingMode.HALF_DOWN)
            wan + "万"
        } else {
            val wan = num.toDouble().divide(100000000.0).format(dot, RoundingMode.HALF_DOWN)
            wan + "亿"
        }
    }
}