package me.wcy.music.utils

import android.graphics.Bitmap

/**
 * 图像工具类
 * Created by wcy on 2015/11/29.
 */
object ImageUtils {

    /**
     * 将图片放大或缩小到指定尺寸
     */
    fun resizeImage(source: Bitmap, dstWidth: Int, dstHeight: Int): Bitmap {
        return if (source.width == dstWidth && source.height == dstHeight) {
            source
        } else {
            Bitmap.createScaledBitmap(source, dstWidth, dstHeight, true)
        }
    }
}