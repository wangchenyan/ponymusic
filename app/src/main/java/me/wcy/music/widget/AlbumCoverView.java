package me.wcy.music.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
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
public class AlbumCoverView extends View {
    private static final long TIME_UPDATE = 50L;
    private static final float ROTATION_INCREASE = 0.5f;
    private Bitmap mDiscBitmap;
    private Bitmap mCoverBitmap;
    private Matrix mDiscMatrix;
    private Matrix mCoverMatrix;
    private Handler mHandler;
    private float mRotation = 0.0f;
    private boolean mIsPlaying = false;

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
        mDiscBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_play_page_disc);
        mDiscBitmap = ImageUtils.resizeImage(mDiscBitmap, Utils.getScreenWidth() * 3 / 4, Utils.getScreenWidth() * 3 / 4);
        mCoverBitmap = CoverLoader.getInstance().loadRound(null);
        mDiscMatrix = new Matrix();
        mCoverMatrix = new Matrix();
        mHandler = new Handler();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // 设置旋转角度
        mDiscMatrix.setRotate(mRotation, getWidth() / 2, getHeight() / 2);
        mCoverMatrix.setRotate(mRotation, getWidth() / 2, getHeight() / 2);
        // 设置初始位置
        mDiscMatrix.preTranslate((getWidth() - mDiscBitmap.getWidth()) / 2, (getHeight() - mDiscBitmap.getHeight()) / 2);
        mCoverMatrix.preTranslate((getWidth() - mCoverBitmap.getWidth()) / 2, (getHeight() - mCoverBitmap.getHeight()) / 2);
        canvas.drawBitmap(mCoverBitmap, mCoverMatrix, null);
        canvas.drawBitmap(mDiscBitmap, mDiscMatrix, null);
    }

    public void setCoverBitmap(Bitmap bitmap) {
        mCoverBitmap = bitmap;
        mRotation = 0.0f;
        mRunnable.run();
        invalidate();
    }

    public void start() {
        if (mIsPlaying) {
            return;
        }
        mIsPlaying = true;
    }

    public void pause() {
        if (!mIsPlaying) {
            return;
        }
        mIsPlaying = false;
    }

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if (mIsPlaying) {
                mRotation += ROTATION_INCREASE;
                if (mRotation >= 360) {
                    mRotation = 0;
                }
                invalidate();
            }
            mHandler.postDelayed(this, TIME_UPDATE);
        }
    };
}
