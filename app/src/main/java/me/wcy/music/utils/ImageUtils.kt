package me.wcy.music.utils

import android.graphics.Bitmap
import android.widget.ImageView
import androidx.core.graphics.scale
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import me.wcy.music.R
import top.wangchenyan.common.ext.load

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
            source.scale(dstWidth, dstHeight)
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

    /**
     * 设置图片的透明度从上到下渐变
     * @param topToBottom 是否为从上到下变为透明
     */
    fun Bitmap.transAlpha(topToBottom: Boolean): Bitmap {
        val argb = IntArray(this.width * this.height)

        // 获得图片的ARGB值
        this.getPixels(
            argb,
            0,
            this.width,
            0,
            0,
            this.width,
            this.height
        )

        // number的范围为0-100,0为全透明，100为不透明
        var number = 100f
        // 透明度数值
        var alpha = number * 255 / 100
        // 透明度渐变梯度，每次随着Y坐标改变的量，因为最终在边缘处要变为0
        val step = number / this.height

        val range = if (topToBottom) {
            argb.indices
        } else {
            argb.size - 1 downTo 0
        }

        for (i in range) {
            // 同一行alpha数值不改变，因为是随着Y坐标从上到下改变的
            if (i % this.width == 0) {
                number -= step
                alpha = number * 255 / 100
            }
            argb[i] = (alpha.toInt() shl 24) or (argb[i] and 0x00FFFFFF)
        }

        return Bitmap.createBitmap(argb, this.width, this.height, Bitmap.Config.ARGB_8888)
    }
}