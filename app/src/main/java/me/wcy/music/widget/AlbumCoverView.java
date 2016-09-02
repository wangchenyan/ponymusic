package me.wcy.music.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowManager;

import me.wcy.music.R;
import me.wcy.music.utils.CoverLoader;
import me.wcy.music.utils.ImageUtils;

/**
 * 专辑封面
 * Created by wcy on 2015/11/30.
 */
public class AlbumCoverView extends View implements ValueAnimator.AnimatorUpdateListener {
    private static final long TIME_UPDATE = 50L;
    private static final float DISC_ROTATION_INCREASE = 0.5f;
    private static final float NEEDLE_ROTATION_PLAY = 0.0f;
    private static final float NEEDLE_ROTATION_PAUSE = -25.0f;
    private Handler mHandler = new Handler();
    private Bitmap mDiscBitmap;
    private Bitmap mCoverBitmap;
    private Bitmap mNeedleBitmap;
    private Drawable mTopLine;
    private Drawable mCoverBorder;
    private int mTopLineHeight;
    private int mCoverBorderWidth;
    private Matrix mDiscMatrix = new Matrix();
    private Matrix mCoverMatrix = new Matrix();
    private Matrix mNeedleMatrix = new Matrix();
    private ValueAnimator mPlayAnimator;
    private ValueAnimator mPauseAnimator;
    private float mDiscRotation = 0.0f;
    private float mNeedleRotation = NEEDLE_ROTATION_PLAY;
    private boolean isPlaying = false;

    // 图片起始坐标
    private Point mDiscPoint = new Point();
    private Point mCoverPoint = new Point();
    private Point mNeedlePoint = new Point();
    // 旋转中心坐标
    private Point mDiscCenterPoint = new Point();
    private Point mCoverCenterPoint = new Point();
    private Point mNeedleCenterPoint = new Point();

    public AlbumCoverView(Context context) {
        this(context, null);
    }

    public AlbumCoverView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AlbumCoverView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mTopLine = getResources().getDrawable(R.drawable.play_page_cover_top_line_shape);
        mCoverBorder = getResources().getDrawable(R.drawable.play_page_cover_border_shape);
        mDiscBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.play_page_disc);
        mDiscBitmap = ImageUtils.resizeImage(mDiscBitmap, (int) (getScreenWidth() * 0.75),
                (int) (getScreenWidth() * 0.75));
        mCoverBitmap = CoverLoader.getInstance().loadRound(null);
        mNeedleBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.play_page_needle);
        mNeedleBitmap = ImageUtils.resizeImage(mNeedleBitmap, (int) (getScreenWidth() * 0.25),
                (int) (getScreenWidth() * 0.375));
        mTopLineHeight = dp2px(1);
        mCoverBorderWidth = dp2px(1);

        mPlayAnimator = ValueAnimator.ofFloat(NEEDLE_ROTATION_PAUSE, NEEDLE_ROTATION_PLAY);
        mPlayAnimator.setDuration(300);
        mPlayAnimator.addUpdateListener(this);
        mPauseAnimator = ValueAnimator.ofFloat(NEEDLE_ROTATION_PLAY, NEEDLE_ROTATION_PAUSE);
        mPauseAnimator.setDuration(300);
        mPauseAnimator.addUpdateListener(this);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        initSize();
    }

    /**
     * 确定图片起始坐标与旋转中心坐标
     */
    private void initSize() {
        int discOffsetY = mNeedleBitmap.getHeight() / 2;
        mDiscPoint.x = (getWidth() - mDiscBitmap.getWidth()) / 2;
        mDiscPoint.y = discOffsetY;
        mCoverPoint.x = (getWidth() - mCoverBitmap.getWidth()) / 2;
        mCoverPoint.y = discOffsetY + (mDiscBitmap.getHeight() - mCoverBitmap.getHeight()) / 2;
        mNeedlePoint.x = getWidth() / 2 - mNeedleBitmap.getWidth() / 6;
        mNeedlePoint.y = -mNeedleBitmap.getWidth() / 6;
        mDiscCenterPoint.x = getWidth() / 2;
        mDiscCenterPoint.y = mDiscBitmap.getHeight() / 2 + discOffsetY;
        mCoverCenterPoint.x = mDiscCenterPoint.x;
        mCoverCenterPoint.y = mDiscCenterPoint.y;
        mNeedleCenterPoint.x = mDiscCenterPoint.x;
        mNeedleCenterPoint.y = 0;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // 1.绘制顶部虚线
        mTopLine.setBounds(0, 0, getWidth(), mTopLineHeight);
        mTopLine.draw(canvas);
        // 2.绘制黑胶唱片外侧半透明边框
        mCoverBorder.setBounds(mDiscPoint.x - mCoverBorderWidth, mDiscPoint.y - mCoverBorderWidth,
                mDiscPoint.x + mDiscBitmap.getWidth() + mCoverBorderWidth, mDiscPoint.y +
                        mDiscBitmap.getHeight() + mCoverBorderWidth);
        mCoverBorder.draw(canvas);
        // 3.绘制黑胶
        // 设置旋转中心和旋转角度，setRotate和preTranslate顺序很重要
        mDiscMatrix.setRotate(mDiscRotation, mDiscCenterPoint.x, mDiscCenterPoint.y);
        // 设置图片起始坐标
        mDiscMatrix.preTranslate(mDiscPoint.x, mDiscPoint.y);
        canvas.drawBitmap(mDiscBitmap, mDiscMatrix, null);
        // 4.绘制封面
        mCoverMatrix.setRotate(mDiscRotation, mCoverCenterPoint.x, mCoverCenterPoint.y);
        mCoverMatrix.preTranslate(mCoverPoint.x, mCoverPoint.y);
        canvas.drawBitmap(mCoverBitmap, mCoverMatrix, null);
        // 5.绘制指针
        mNeedleMatrix.setRotate(mNeedleRotation, mNeedleCenterPoint.x, mNeedleCenterPoint.y);
        mNeedleMatrix.preTranslate(mNeedlePoint.x, mNeedlePoint.y);
        canvas.drawBitmap(mNeedleBitmap, mNeedleMatrix, null);
    }

    public void initNeedle(boolean isPlaying) {
        mNeedleRotation = isPlaying ? NEEDLE_ROTATION_PLAY : NEEDLE_ROTATION_PAUSE;
        invalidate();
    }

    public void setCoverBitmap(Bitmap bitmap) {
        mCoverBitmap = bitmap;
        mDiscRotation = 0.0f;
        invalidate();
    }

    public void start() {
        if (isPlaying) {
            return;
        }
        isPlaying = true;
        mHandler.post(mRotationRunnable);
        mPlayAnimator.start();
    }

    public void pause() {
        if (!isPlaying) {
            return;
        }
        isPlaying = false;
        mHandler.removeCallbacks(mRotationRunnable);
        mPauseAnimator.start();
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        mNeedleRotation = (float) animation.getAnimatedValue();
        invalidate();
    }

    private Runnable mRotationRunnable = new Runnable() {
        @Override
        public void run() {
            if (isPlaying) {
                mDiscRotation += DISC_ROTATION_INCREASE;
                if (mDiscRotation >= 360) {
                    mDiscRotation = 0;
                }
                invalidate();
            }
            mHandler.postDelayed(this, TIME_UPDATE);
        }
    };

    private int getScreenWidth() {
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        return wm.getDefaultDisplay().getWidth();
    }

    private int dp2px(float dpValue) {
        float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
