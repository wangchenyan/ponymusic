package me.wcy.music.widget

import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Point
import android.graphics.drawable.Drawable
import android.os.Handler
import android.util.AttributeSet
import android.view.View
import me.wcy.music.R
import me.wcy.music.utils.CoverLoader
import me.wcy.music.utils.ImageUtils

/**
 * 专辑封面
 * Created by wcy on 2015/11/30.
 */
class AlbumCoverView @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr), AnimatorUpdateListener {
    private val mHandler = Handler()
    private var mDiscBitmap: Bitmap? = null
    private var mCoverBitmap: Bitmap? = null
    private var mNeedleBitmap: Bitmap? = null
    private var mTopLine: Drawable? = null
    private var mCoverBorder: Drawable? = null
    private var mTopLineHeight = 0
    private var mCoverBorderWidth = 0
    private val mDiscMatrix = Matrix()
    private val mCoverMatrix = Matrix()
    private val mNeedleMatrix = Matrix()
    private lateinit var mPlayAnimator: ValueAnimator
    private lateinit var mPauseAnimator: ValueAnimator
    private var mDiscRotation = 0.0f
    private var mNeedleRotation = NEEDLE_ROTATION_PLAY
    private var isPlaying = false

    // 图片起始坐标
    private val mDiscPoint = Point()
    private val mCoverPoint = Point()
    private val mNeedlePoint = Point()

    // 旋转中心坐标
    private val mDiscCenterPoint = Point()
    private val mCoverCenterPoint = Point()
    private val mNeedleCenterPoint = Point()
    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (changed) {
            initOnLayout()
        }
    }

    private fun init() {
        mTopLine = resources.getDrawable(R.drawable.play_page_cover_top_line_shape)
        mCoverBorder = resources.getDrawable(R.drawable.play_page_cover_border_shape)
        mDiscBitmap = BitmapFactory.decodeResource(resources, R.drawable.play_page_disc)
        mCoverBitmap = CoverLoader.get().loadRound(null)
        mNeedleBitmap = BitmapFactory.decodeResource(resources, R.drawable.play_page_needle)
        mTopLineHeight = dp2px(1f)
        mCoverBorderWidth = dp2px(1f)
        mPlayAnimator = ValueAnimator.ofFloat(NEEDLE_ROTATION_PAUSE, NEEDLE_ROTATION_PLAY)
        mPlayAnimator.setDuration(300)
        mPlayAnimator.addUpdateListener(this)
        mPauseAnimator = ValueAnimator.ofFloat(NEEDLE_ROTATION_PLAY, NEEDLE_ROTATION_PAUSE)
        mPauseAnimator.setDuration(300)
        mPauseAnimator.addUpdateListener(this)
    }

    private fun initOnLayout() {
        if (width == 0 || height == 0) {
            return
        }
        val unit = Math.min(width, height) / 8
        CoverLoader.get().setRoundLength(unit * 4)
        mDiscBitmap = ImageUtils.resizeImage(mDiscBitmap, unit * 6, unit * 6)
        mCoverBitmap = ImageUtils.resizeImage(mCoverBitmap, unit * 4, unit * 4)
        mNeedleBitmap = ImageUtils.resizeImage(mNeedleBitmap, unit * 2, unit * 3)
        val discOffsetY = mNeedleBitmap!!.height / 2
        mDiscPoint.x = (width - mDiscBitmap!!.width) / 2
        mDiscPoint.y = discOffsetY
        mCoverPoint.x = (width - mCoverBitmap!!.width) / 2
        mCoverPoint.y = discOffsetY + (mDiscBitmap!!.height - mCoverBitmap!!.height) / 2
        mNeedlePoint.x = width / 2 - mNeedleBitmap!!.width / 6
        mNeedlePoint.y = -mNeedleBitmap!!.width / 6
        mDiscCenterPoint.x = width / 2
        mDiscCenterPoint.y = mDiscBitmap!!.height / 2 + discOffsetY
        mCoverCenterPoint.x = mDiscCenterPoint.x
        mCoverCenterPoint.y = mDiscCenterPoint.y
        mNeedleCenterPoint.x = mDiscCenterPoint.x
        mNeedleCenterPoint.y = 0
    }

    override fun onDraw(canvas: Canvas) {
        // 1.绘制顶部虚线
        mTopLine!!.setBounds(0, 0, width, mTopLineHeight)
        mTopLine!!.draw(canvas)
        // 2.绘制黑胶唱片外侧半透明边框
        mCoverBorder!!.setBounds(
            mDiscPoint.x - mCoverBorderWidth,
            mDiscPoint.y - mCoverBorderWidth,
            mDiscPoint.x + mDiscBitmap!!.width + mCoverBorderWidth,
            mDiscPoint.y + mDiscBitmap!!.height + mCoverBorderWidth
        )
        mCoverBorder!!.draw(canvas)
        // 3.绘制黑胶
        // 设置旋转中心和旋转角度，setRotate和preTranslate顺序很重要
        mDiscMatrix.setRotate(
            mDiscRotation,
            mDiscCenterPoint.x.toFloat(),
            mDiscCenterPoint.y.toFloat()
        )
        // 设置图片起始坐标
        mDiscMatrix.preTranslate(mDiscPoint.x.toFloat(), mDiscPoint.y.toFloat())
        canvas.drawBitmap(mDiscBitmap!!, mDiscMatrix, null)
        // 4.绘制封面
        mCoverMatrix.setRotate(
            mDiscRotation,
            mCoverCenterPoint.x.toFloat(),
            mCoverCenterPoint.y.toFloat()
        )
        mCoverMatrix.preTranslate(mCoverPoint.x.toFloat(), mCoverPoint.y.toFloat())
        canvas.drawBitmap(mCoverBitmap!!, mCoverMatrix, null)
        // 5.绘制指针
        mNeedleMatrix.setRotate(
            mNeedleRotation,
            mNeedleCenterPoint.x.toFloat(),
            mNeedleCenterPoint.y.toFloat()
        )
        mNeedleMatrix.preTranslate(mNeedlePoint.x.toFloat(), mNeedlePoint.y.toFloat())
        canvas.drawBitmap(mNeedleBitmap!!, mNeedleMatrix, null)
    }

    fun initNeedle(isPlaying: Boolean) {
        mNeedleRotation = if (isPlaying) NEEDLE_ROTATION_PLAY else NEEDLE_ROTATION_PAUSE
        invalidate()
    }

    fun setCoverBitmap(bitmap: Bitmap?) {
        mCoverBitmap = bitmap
        mDiscRotation = 0.0f
        invalidate()
    }

    fun start() {
        if (isPlaying) {
            return
        }
        isPlaying = true
        mHandler.post(mRotationRunnable)
        mPlayAnimator!!.start()
    }

    fun pause() {
        if (!isPlaying) {
            return
        }
        isPlaying = false
        mHandler.removeCallbacks(mRotationRunnable)
        mPauseAnimator!!.start()
    }

    override fun onAnimationUpdate(animation: ValueAnimator) {
        mNeedleRotation = animation.animatedValue as Float
        invalidate()
    }

    private val mRotationRunnable: Runnable = object : Runnable {
        override fun run() {
            if (isPlaying) {
                mDiscRotation += DISC_ROTATION_INCREASE
                if (mDiscRotation >= 360) {
                    mDiscRotation = 0f
                }
                invalidate()
            }
            mHandler.postDelayed(this, TIME_UPDATE)
        }
    }

    init {
        init()
    }

    private fun dp2px(dpValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    companion object {
        private const val TIME_UPDATE = 50L
        private const val DISC_ROTATION_INCREASE = 0.5f
        private const val NEEDLE_ROTATION_PLAY = 0.0f
        private const val NEEDLE_ROTATION_PAUSE = -25.0f
    }
}