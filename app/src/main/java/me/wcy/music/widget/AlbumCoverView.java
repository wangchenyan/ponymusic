package me.wcy.music.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

import me.wcy.music.R;
import me.wcy.music.utils.CoverLoader;
import me.wcy.music.utils.ImageUtils;
import me.wcy.music.utils.ScreenUtils;

/**
 * 专辑封面
 * Created by wcy on 2015/11/30.
 */
public class AlbumCoverView extends View implements ValueAnimator.AnimatorUpdateListener {
    private static final long TIME_UPDATE = 50L;
    private static final float DISC_ROTATION_INCREASE = 0.5f;
    private static final float NEEDLE_ROTATION_PLAY = 0.0f;
    private static final float NEEDLE_ROTATION_PAUSE = -25.0f;
    private Handler mHandler;
    private Bitmap mDiscBitmap;
    private Bitmap mCoverBitmap;
    private Bitmap mNeedleBitmap;
    private Drawable mTopLine;
    private Drawable mCoverBorder;
    private Matrix mDiscMatrix;
    private Matrix mCoverMatrix;
    private Matrix mNeedleMatrix;
    private ValueAnimator mPlayAnimator;
    private ValueAnimator mPauseAnimator;
    private float mDiscRotation = 0.0f;
    private float mNeedleRotation = NEEDLE_ROTATION_PLAY;
    private boolean mIsPlaying = false;

    private int mTopLineHeight = ScreenUtils.dp2px(1);
    private int mCoverBorderWidth = ScreenUtils.dp2px(1);
    // 圆心坐标
    private float mDiscPX;
    private float mDiscPY;
    private float mCoverPX;
    private float mCoverPY;
    private float mNeedlePX;
    private float mNeedlePY;
    // 旋转半径端点坐标
    private float mDiscDX;
    private float mDiscDY;
    private float mCoverDX;
    private float mCoverDY;
    private float mNeedleDX;
    private float mNeedleDY;

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
        mHandler = new Handler();
        mTopLine = getResources().getDrawable(R.drawable.play_page_cover_top_line_shape);
        mCoverBorder = getResources().getDrawable(R.drawable.play_page_cover_border_shape);
        mDiscBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.play_page_disc);
        mDiscBitmap = ImageUtils.resizeImage(mDiscBitmap, (int) (ScreenUtils.getScreenWidth() * 0.75),
                (int) (ScreenUtils.getScreenWidth() * 0.75));
        mCoverBitmap = CoverLoader.getInstance().loadRound(null);
        mNeedleBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.play_page_needle);
        mNeedleBitmap = ImageUtils.resizeImage(mNeedleBitmap, (int) (ScreenUtils.getScreenWidth() * 0.25),
                (int) (ScreenUtils.getScreenWidth() * 0.375));
        mDiscMatrix = new Matrix();
        mCoverMatrix = new Matrix();
        mNeedleMatrix = new Matrix();
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
     * 确定圆心坐标与旋转半径端点坐标
     */
    private void initSize() {
        int discOffsetY = mNeedleBitmap.getHeight() / 2;
        mDiscPX = getWidth() / 2;
        mDiscPY = mDiscBitmap.getHeight() / 2 + discOffsetY;
        mCoverPX = mDiscPX;
        mCoverPY = mDiscPY;
        mNeedlePX = mDiscPX;
        mNeedlePY = 0;
        mDiscDX = (getWidth() - mDiscBitmap.getWidth()) / 2;
        mDiscDY = discOffsetY;
        mCoverDX = (getWidth() - mCoverBitmap.getWidth()) / 2;
        mCoverDY = discOffsetY + (mDiscBitmap.getHeight() - mCoverBitmap.getHeight()) / 2;
        mNeedleDX = getWidth() / 2 - mNeedleBitmap.getWidth() / 6;
        mNeedleDY = -mNeedleBitmap.getWidth() / 6;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // 1.绘制顶部虚线
        mTopLine.setBounds(0, getTop(), getWidth(), getTop() + mTopLineHeight);
        mTopLine.draw(canvas);
        // 2.绘制黑胶唱片外侧半透明边框
        mCoverBorder.setBounds((int) mDiscDX - mCoverBorderWidth, (int) mDiscDY - mCoverBorderWidth,
                (int) mDiscDX + mDiscBitmap.getWidth() + mCoverBorderWidth, (int) mDiscDY +
                        mDiscBitmap.getHeight() + mCoverBorderWidth);
        mCoverBorder.draw(canvas);
        // 3.绘制黑胶
        // 设置旋转角度和圆心
        mDiscMatrix.setRotate(mDiscRotation, mDiscPX, mDiscPY);
        // 设置旋转半径端点坐标
        mDiscMatrix.preTranslate(mDiscDX, mDiscDY);
        canvas.drawBitmap(mDiscBitmap, mDiscMatrix, null);
        // 4.绘制封面
        mCoverMatrix.setRotate(mDiscRotation, mCoverPX, mCoverPY);
        mCoverMatrix.preTranslate(mCoverDX, mCoverDY);
        canvas.drawBitmap(mCoverBitmap, mCoverMatrix, null);
        // 5.绘制指针
        mNeedleMatrix.setRotate(mNeedleRotation, mNeedlePX, mNeedlePY);
        mNeedleMatrix.preTranslate(mNeedleDX, mNeedleDY);
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
        if (mIsPlaying) {
            return;
        }
        mIsPlaying = true;
        mHandler.post(mRotationRunnable);
        mPlayAnimator.start();
    }

    public void pause() {
        if (!mIsPlaying) {
            return;
        }
        mIsPlaying = false;
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
            if (mIsPlaying) {
                mDiscRotation += DISC_ROTATION_INCREASE;
                if (mDiscRotation >= 360) {
                    mDiscRotation = 0;
                }
                invalidate();
            }
            mHandler.postDelayed(this, TIME_UPDATE);
        }
    };
}
