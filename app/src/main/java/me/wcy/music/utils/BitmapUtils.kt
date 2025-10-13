package me.wcy.music.utils

import android.content.Context
import android.graphics.Bitmap
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import androidx.core.graphics.createBitmap

/**
 * Created by wangchenyan.top on 2025/10/13.
 */
object BitmapUtils {
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

    /**
     * 高斯模糊处理
     * @param radius 模糊半径 0~25
     */
    fun Bitmap.blur(context: Context, radius: Float = 25f): Bitmap {
        val renderScript = RenderScript.create(context)
        val input = Allocation.createFromBitmap(renderScript, this)
        val output = Allocation.createTyped(renderScript, input.type)
        val script = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript))

        script.setRadius(radius.coerceIn(0f, 25f)) // 模糊半径 0~25
        script.setInput(input)
        script.forEach(output)

        val config = this.config ?: Bitmap.Config.ARGB_8888
        val blurredBitmap = createBitmap(this.width, this.height, config)
        output.copyTo(blurredBitmap)

        renderScript.destroy()
        return blurredBitmap
    }
}