package me.wcy.ponymusic.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

import me.wcy.ponymusic.R;
import me.wcy.ponymusic.utils.MusicUtils;

/**
 * 专辑封面
 * Created by wcy on 2015/11/30.
 */
public class AlbumCoverView extends View {
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
        super(context, attrs);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.outWidth = MusicUtils.getScreenWidth() * 3 / 4;
        options.outHeight = MusicUtils.getScreenWidth() * 3 / 4;
        mDiscBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_play_page_disc, options);
        mDiscMatrix = new Matrix();
        mCoverMatrix = new Matrix();
        mHandler = new CoverHandler();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //设置旋转角度
        mDiscMatrix.setRotate(mRotation, getWidth() / 2, getHeight() / 2);
        mCoverMatrix.setRotate(mRotation, getWidth() / 2, getHeight() / 2);
        //设置初始位置
        mDiscMatrix.preTranslate((getWidth() - mDiscBitmap.getWidth()) / 2, (getHeight() - mDiscBitmap.getHeight()) / 2);
        mCoverMatrix.preTranslate((getWidth() - mCoverBitmap.getWidth()) / 2, (getHeight() - mCoverBitmap.getHeight()) / 2);
        canvas.drawBitmap(mCoverBitmap, mCoverMatrix, null);
        canvas.drawBitmap(mDiscBitmap, mDiscMatrix, null);
    }

    public void setCoverBitmap(Bitmap bitmap) {
        mCoverBitmap = bitmap;
        mRotation = 0.0f;
        invalidate();
    }

    public void start() {
        if (mIsPlaying) {
            return;
        }
        mIsPlaying = true;
        mHandler.sendEmptyMessageDelayed(0, 50);
    }

    public void pause() {
        if (!mIsPlaying) {
            return;
        }
        mIsPlaying = false;
    }

    private class CoverHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                if (!mIsPlaying) {
                    return;
                }
                mRotation += 0.5f;
                if (mRotation >= 360) {
                    mRotation = 0;
                }
                invalidate();
                sendEmptyMessageDelayed(0, 50);
            }
        }
    }
}
