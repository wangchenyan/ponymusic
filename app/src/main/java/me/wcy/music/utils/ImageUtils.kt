package me.wcy.music.utils

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.net.Uri
import android.provider.MediaStore
import me.wcy.music.const.RequestCode
import java.io.File

/**
 * 图像工具类
 * Created by wcy on 2015/11/29.
 */
object ImageUtils {
    private const val BLUR_RADIUS = 50

    fun blur(source: Bitmap?): Bitmap? {
        return if (source == null) {
            null
        } else try {
            blur(source, BLUR_RADIUS)
        } catch (e: Exception) {
            e.printStackTrace()
            source
        }
    }

    /**
     * Stack Blur v1.0 from
     * http://www.quasimondo.com/StackBlurForCanvas/StackBlurDemo.html
     *
     *
     * Java Author: Mario Klingemann <mario at quasimondo.com>
     * http://incubator.quasimondo.com
     * created Feburary 29, 2004
     * Android port : Yahel Bouaziz <yahel at kayenko.com>
     * http://www.kayenko.com
     * ported april 5th, 2012
    </yahel></mario> *
     *
     * This is a compromise between Gaussian Blur and Box blur
     * It creates much better looking blurs than Box Blur, but is
     * 7x faster than my Gaussian Blur implementation.
     *
     *
     * I called it Stack Blur because this describes best how this
     * filter works internally: it creates a kind of moving stack
     * of colors whilst scanning through the image. Thereby it
     * just has to add one new block of color to the right side
     * of the stack and remove the leftmost color. The remaining
     * colors on the topmost layer of the stack are either added on
     * or reduced by one, depending on if they are on the right or
     * on the left side of the stack.
     *
     *
     * If you are using this algorithm in your code please add
     * the following line:
     *
     *
     * Stack Blur Algorithm by Mario Klingemann <mario></mario>@quasimondo.com>
     */
    private fun blur(source: Bitmap, radius: Int): Bitmap? {
        val bitmap = source.copy(source.config, true)
        if (radius < 1) {
            return null
        }
        val w = bitmap.width
        val h = bitmap.height
        val pix = IntArray(w * h)
        bitmap.getPixels(pix, 0, w, 0, 0, w, h)
        val wm = w - 1
        val hm = h - 1
        val wh = w * h
        val div = radius + radius + 1
        val r = IntArray(wh)
        val g = IntArray(wh)
        val b = IntArray(wh)
        var rSum: Int
        var gSum: Int
        var bSum: Int
        var x: Int
        var y: Int
        var i: Int
        var p: Int
        var yp: Int
        var yi: Int
        var yw: Int
        val vMin = IntArray(Math.max(w, h))
        var divSum = div + 1 shr 1
        divSum *= divSum
        val dv = IntArray(256 * divSum)
        i = 0
        while (i < 256 * divSum) {
            dv[i] = i / divSum
            i++
        }
        yi = 0
        yw = yi
        val stack = Array(div) { IntArray(3) }
        var stackPointer: Int
        var stackStart: Int
        var sir: IntArray
        var rbs: Int
        val r1 = radius + 1
        var rOutSum: Int
        var gOutSum: Int
        var bOutSum: Int
        var rInSum: Int
        var gInSum: Int
        var bInSum: Int
        y = 0
        while (y < h) {
            bSum = 0
            gSum = bSum
            rSum = gSum
            bOutSum = rSum
            gOutSum = bOutSum
            rOutSum = gOutSum
            bInSum = rOutSum
            gInSum = bInSum
            rInSum = gInSum
            i = -radius
            while (i <= radius) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))]
                sir = stack[i + radius]
                sir[0] = p and 0xff0000 shr 16
                sir[1] = p and 0x00ff00 shr 8
                sir[2] = p and 0x0000ff
                rbs = r1 - Math.abs(i)
                rSum += sir[0] * rbs
                gSum += sir[1] * rbs
                bSum += sir[2] * rbs
                if (i > 0) {
                    rInSum += sir[0]
                    gInSum += sir[1]
                    bInSum += sir[2]
                } else {
                    rOutSum += sir[0]
                    gOutSum += sir[1]
                    bOutSum += sir[2]
                }
                i++
            }
            stackPointer = radius
            x = 0
            while (x < w) {
                r[yi] = dv[rSum]
                g[yi] = dv[gSum]
                b[yi] = dv[bSum]
                rSum -= rOutSum
                gSum -= gOutSum
                bSum -= bOutSum
                stackStart = stackPointer - radius + div
                sir = stack[stackStart % div]
                rOutSum -= sir[0]
                gOutSum -= sir[1]
                bOutSum -= sir[2]
                if (y == 0) {
                    vMin[x] = Math.min(x + radius + 1, wm)
                }
                p = pix[yw + vMin[x]]
                sir[0] = p and 0xff0000 shr 16
                sir[1] = p and 0x00ff00 shr 8
                sir[2] = p and 0x0000ff
                rInSum += sir[0]
                gInSum += sir[1]
                bInSum += sir[2]
                rSum += rInSum
                gSum += gInSum
                bSum += bInSum
                stackPointer = (stackPointer + 1) % div
                sir = stack[stackPointer % div]
                rOutSum += sir[0]
                gOutSum += sir[1]
                bOutSum += sir[2]
                rInSum -= sir[0]
                gInSum -= sir[1]
                bInSum -= sir[2]
                yi++
                x++
            }
            yw += w
            y++
        }
        x = 0
        while (x < w) {
            bSum = 0
            gSum = bSum
            rSum = gSum
            bOutSum = rSum
            gOutSum = bOutSum
            rOutSum = gOutSum
            bInSum = rOutSum
            gInSum = bInSum
            rInSum = gInSum
            yp = -radius * w
            i = -radius
            while (i <= radius) {
                yi = Math.max(0, yp) + x
                sir = stack[i + radius]
                sir[0] = r[yi]
                sir[1] = g[yi]
                sir[2] = b[yi]
                rbs = r1 - Math.abs(i)
                rSum += r[yi] * rbs
                gSum += g[yi] * rbs
                bSum += b[yi] * rbs
                if (i > 0) {
                    rInSum += sir[0]
                    gInSum += sir[1]
                    bInSum += sir[2]
                } else {
                    rOutSum += sir[0]
                    gOutSum += sir[1]
                    bOutSum += sir[2]
                }
                if (i < hm) {
                    yp += w
                }
                i++
            }
            yi = x
            stackPointer = radius
            y = 0
            while (y < h) {

                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] =
                    -0x1000000 and pix[yi] or (dv[rSum] shl 16) or (dv[gSum] shl 8) or dv[bSum]
                rSum -= rOutSum
                gSum -= gOutSum
                bSum -= bOutSum
                stackStart = stackPointer - radius + div
                sir = stack[stackStart % div]
                rOutSum -= sir[0]
                gOutSum -= sir[1]
                bOutSum -= sir[2]
                if (x == 0) {
                    vMin[y] = Math.min(y + r1, hm) * w
                }
                p = x + vMin[y]
                sir[0] = r[p]
                sir[1] = g[p]
                sir[2] = b[p]
                rInSum += sir[0]
                gInSum += sir[1]
                bInSum += sir[2]
                rSum += rInSum
                gSum += gInSum
                bSum += bInSum
                stackPointer = (stackPointer + 1) % div
                sir = stack[stackPointer]
                rOutSum += sir[0]
                gOutSum += sir[1]
                bOutSum += sir[2]
                rInSum -= sir[0]
                gInSum -= sir[1]
                bInSum -= sir[2]
                yi += w
                y++
            }
            x++
        }
        bitmap.setPixels(pix, 0, w, 0, 0, w, h)
        return bitmap
    }

    /**
     * 将图片放大或缩小到指定尺寸
     */
    fun resizeImage(source: Bitmap, dstWidth: Int, dstHeight: Int): Bitmap {
        return if (source.width == dstWidth && source.height == dstHeight) {
            source
        } else Bitmap.createScaledBitmap(source, dstWidth, dstHeight, true)
    }

    /**
     * 将图片剪裁为圆形
     */
    fun createCircleImage(source: Bitmap?): Bitmap? {
        if (source == null) {
            return null
        }
        val length = Math.min(source.width, source.height)
        val paint = Paint()
        paint.isAntiAlias = true
        val target = Bitmap.createBitmap(length, length, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(target)
        canvas.drawCircle(
            (source.width / 2).toFloat(),
            (source.height / 2).toFloat(),
            (length / 2).toFloat(),
            paint
        )
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(source, 0f, 0f, paint)
        return target
    }

    fun startCorp(activity: Activity, uri: Uri?) {
        val intent = Intent("com.android.camera.action.CROP")
        intent.setDataAndType(uri, "image/*")
        intent.putExtra("crop", "true")
        intent.putExtra("scale", true)
        intent.putExtra("aspectX", 1)
        intent.putExtra("aspectY", 1)
        intent.putExtra("outputX", CoverLoader.THUMBNAIL_MAX_LENGTH)
        intent.putExtra("outputY", CoverLoader.THUMBNAIL_MAX_LENGTH)
        intent.putExtra("return-data", false)
        val outFile = File(FileUtils.getCorpImagePath(activity))
        val outUri = Uri.fromFile(outFile)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outUri)
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString())
        // 取消人脸识别
        intent.putExtra("noFaceDetection", true)
        activity.startActivityForResult(intent, RequestCode.REQUEST_CORP)
    }
}