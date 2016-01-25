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
import me.wcy.music.utils.Utils;

/**
 * 专辑封面
 * Created by wcy on 2015/11/30.
 */
public class AlbumCoverView extends View implements ValueAnimator.AnimatorUpdateListener {
    private static final long TIME_UPDATE = 50L;
    private static final float DISC_ROTATION_INCREASE = 0.5f;
    private static final float NEEDLE_ROTATION_START = 0.0f;
    private static final float NEEDLE_ROTATION_END = -25.0f;
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
    private float mNeedleRotation = NEEDLE_ROTATION_START;
    private boolean mIsPlaying = false;

    private float mDiscPX = -1.0f;
    private float mDiscPY = -1.0f;
    private float mDiscDX = -1.0f;
    private float mDiscDY = -1.0f;
    private float mCoverPX = -1.0f;
    private float mCoverPY = -1.0f;
    private float mCoverDX = -1.0f;
    private float mCoverDY = -1.0f;
    private float mNeedlePX = -1.0f;
    private float mNeedlePY = -1.0f;
    private float mNeedleDX = -1.0f;
    private float mNeedleDY = -1.0f;
    private int mTopLineHeight = -1;
    private int mCoverBorderWidth = -1;

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
        mDiscBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_play_page_disc);
        mDiscBitmap = ImageUtils.resizeImage(mDiscBitmap, (int) (Utils.getScreenWidth() * 0.75), (int) (Utils.getScreenWidth() * 0.75));
        mCoverBitmap = CoverLoader.getInstance().loadRound(null);
        mNeedleBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_play_page_needle);
        mNeedleBitmap = ImageUtils.resizeImage(mNeedleBitmap, (int) (Utils.getScreenWidth() * 0.25), (int) (Utils.getScreenWidth() * 0.375));
        mTopLine = getResources().getDrawable(R.drawable.ic_play_page_cover_top_line_shape);
        mCoverBorder = getResources().getDrawable(R.drawable.ic_play_page_cover_border_shape);
        mDiscMatrix = new Matrix();
        mCoverMatrix = new Matrix();
        mNeedleMatrix = new Matrix();
        mPlayAnimator = ValueAnimator.ofFloat(NEEDLE_ROTATION_END, NEEDLE_ROTATION_START);
        mPauseAnimator = ValueAnimator.ofFloat(NEEDLE_ROTATION_START, NEEDLE_ROTATION_END);
        mPlayAnimator.setDuration(300);
        mPlayAnimator.addUpdateListener(this);
        mPauseAnimator.setDuration(300);
        mPauseAnimator.addUpdateListener(this);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mDiscPX == -1.0f) {
            // 避免重复计算
            int discOffsetY = mNeedleBitmap.getHeight() / 2;
            mDiscPX = getWidth() / 2;
            mDiscPY = getTop() + mDiscBitmap.getHeight() / 2 + discOffsetY;
            mCoverPX = mDiscPX;
            mCoverPY = mDiscPY;
            mNeedlePX = mDiscPX;
            mNeedlePY = getTop();
            mDiscDX = (getWidth() - mDiscBitmap.getWidth()) / 2;
            mDiscDY = getTop() + discOffsetY;
            mCoverDX = (getWidth() - mCoverBitmap.getWidth()) / 2;
            mCoverDY = getTop() + discOffsetY + (mDiscBitmap.getHeight() - mCoverBitmap.getHeight()) / 2;
            mNeedleDX = getWidth() / 2 - mNeedleBitmap.getWidth() / 6;
            mNeedleDY = getTop() - mNeedleBitmap.getWidth() / 6;
            mTopLineHeight = Utils.dp2px(getContext(), 1);
            mCoverBorderWidth = Utils.dp2px(getContext(), 1);
        }
        // 设置旋转角度和圆心
        mDiscMatrix.setRotate(mDiscRotation, mDiscPX, mDiscPY);
        mCoverMatrix.setRotate(mDiscRotation, mCoverPX, mCoverPY);
        mNeedleMatrix.setRotate(mNeedleRotation, mNeedlePX, mNeedlePY);
        // 设置旋转半径端点坐标
        mDiscMatrix.preTranslate(mDiscDX, mDiscDY);
        mCoverMatrix.preTranslate(mCoverDX, mCoverDY);
        mNeedleMatrix.preTranslate(mNeedleDX, mNeedleDY);
        mTopLine.setBounds(0, getTop(), getWidth(), getTop() + mTopLineHeight);
        mCoverBorder.setBounds((int) mDiscDX - mCoverBorderWidth, (int) mDiscDY - mCoverBorderWidth,
                (int) mDiscDX + mDiscBitmap.getWidth() + mCoverBorderWidth, (int) mDiscDY + mDiscBitmap.getHeight() + mCoverBorderWidth);
        // 绘制
        mTopLine.draw(canvas);
        mCoverBorder.draw(canvas);
        canvas.drawBitmap(mCoverBitmap, mCoverMatrix, null);
        canvas.drawBitmap(mDiscBitmap, mDiscMatrix, null);
        canvas.drawBitmap(mNeedleBitmap, mNeedleMatrix, null);
    }

    public void setInitialData(boolean isPlaying) {
        mNeedleRotation = isPlaying ? NEEDLE_ROTATION_START : NEEDLE_ROTATION_END;
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
        mHandler.post(mRunnable);
        mPlayAnimator.start();
    }

    public void pause() {
        if (!mIsPlaying) {
            return;
        }
        mIsPlaying = false;
        mHandler.removeCallbacks(mRunnable);
        mPauseAnimator.start();
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        mNeedleRotation = (float) animation.getAnimatedValue();
        invalidate();
    }

    private Runnable mRunnable = new Runnable() {
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
