package me.wcy.music.utils

import android.graphics.Bitmap
import android.widget.ImageView
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import top.wangchenyan.common.ext.load
import me.wcy.music.R

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

    fun ImageView.loadCover(url: Any?, corners: Int) {
        load(url) {
            placeholder(R.drawable.ic_default_cover)
            error(R.drawable.ic_default_cover)

            if (corners > 0) {
                // 圆角和 CenterCrop 不兼容，需同时设置
                transform(CenterCrop(), RoundedCorners(corners))
            }
        }
    }
}