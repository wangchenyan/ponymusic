package me.wcy.music.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.util.LruCache;
import android.text.TextUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import me.wcy.music.R;
import me.wcy.music.application.MusicApplication;

/**
 * 专辑封面图片加载器
 * Created by wcy on 2015/11/27.
 */
public class CoverLoader {
    private static final String KEY_NULL = "null";
    // 缩略图，用于音乐列表
    private LruCache<String, Bitmap> mThumbnailCache;
    // 高斯模糊图，用于播放页背景
    private LruCache<String, Bitmap> mBlurCache;
    // 圆形图，用于播放页CD
    private LruCache<String, Bitmap> mRoundCache;

    private CoverLoader() {
        int maxSize = (int) (Runtime.getRuntime().maxMemory() / 8);
        mThumbnailCache = new LruCache<String, Bitmap>(maxSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight();
            }
        };
        mBlurCache = new LruCache<String, Bitmap>(maxSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight();
            }
        };
        mRoundCache = new LruCache<String, Bitmap>(maxSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight();
            }
        };
    }

    public static CoverLoader getInstance() {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private static CoverLoader instance = new CoverLoader();
    }

    public Bitmap loadThumbnail(String uri) {
        Bitmap bitmap;
        if (TextUtils.isEmpty(uri)) {
            bitmap = mThumbnailCache.get(KEY_NULL);
            if (bitmap == null) {
                bitmap = BitmapFactory.decodeResource(MusicApplication.getInstance().getResources(), R.drawable.ic_default_cover);
                mThumbnailCache.put(KEY_NULL, bitmap);
            }
        } else {
            bitmap = mThumbnailCache.get(uri);
            if (bitmap == null) {
                try {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true; // 仅获取大小
                    BitmapFactory.decodeStream(new FileInputStream(uri), null, options);
                    //压缩尺寸，避免卡顿
                    int inSampleSize = options.outHeight / (ScreenUtils.getScreenWidth() / 10);
                    if (inSampleSize <= 1) {
                        inSampleSize = 1;
                    }
                    options.inSampleSize = inSampleSize;
                    options.inJustDecodeBounds = false; // 获取bitmap
                    bitmap = BitmapFactory.decodeStream(new FileInputStream(uri), null, options);
                    bitmap = bitmap == null ? loadThumbnail(null) : bitmap;
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    bitmap = loadThumbnail(null);
                }
                mThumbnailCache.put(uri, bitmap);
            }
        }
        return bitmap;
    }

    public Bitmap loadBlur(String uri) {
        Bitmap bitmap;
        if (TextUtils.isEmpty(uri)) {
            bitmap = mBlurCache.get(KEY_NULL);
            if (bitmap == null) {
                bitmap = BitmapFactory.decodeResource(MusicApplication.getInstance().getResources(), R.drawable.ic_play_page_default_bg);
                mBlurCache.put(KEY_NULL, bitmap);
            }
        } else {
            bitmap = mBlurCache.get(uri);
            if (bitmap == null) {
                bitmap = loadNormal(uri);
                if (bitmap == null) {
                    bitmap = loadBlur(null);
                } else {
                    bitmap = ImageUtils.stackBlur(bitmap, ImageUtils.BLUR_RADIUS);
                }
                mBlurCache.put(uri, bitmap);
            }
        }
        return bitmap;
    }

    public Bitmap loadRound(String uri) {
        Bitmap bitmap;
        if (TextUtils.isEmpty(uri)) {
            bitmap = mRoundCache.get(KEY_NULL);
            if (bitmap == null) {
                bitmap = BitmapFactory.decodeResource(MusicApplication.getInstance().getResources(), R.drawable.ic_play_page_default_cover);
                bitmap = ImageUtils.resizeImage(bitmap, ScreenUtils.getScreenWidth() / 2, ScreenUtils.getScreenWidth() / 2);
                mRoundCache.put(KEY_NULL, bitmap);
            }
        } else {
            bitmap = mRoundCache.get(uri);
            if (bitmap == null) {
                bitmap = loadNormal(uri);
                if (bitmap == null) {
                    bitmap = loadRound(null);
                } else {
                    bitmap = ImageUtils.resizeImage(bitmap, ScreenUtils.getScreenWidth() / 2, ScreenUtils.getScreenWidth() / 2);
                    bitmap = ImageUtils.createCircleImage(bitmap);
                }
                mRoundCache.put(uri, bitmap);
            }
        }
        return bitmap;
    }

    /**
     * 获得最大宽度为屏幕一半的图片,如果超出，则缩放
     */
    private Bitmap loadNormal(String uri) {
        Bitmap bitmap = null;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            bitmap = BitmapFactory.decodeStream(new FileInputStream(uri), null, options);
            // 压缩尺寸，避免卡顿
            int inSampleSize = options.outWidth / (ScreenUtils.getScreenWidth() / 2);
            if (inSampleSize <= 0) {
                inSampleSize = 1;
            }
            options.inSampleSize = inSampleSize;
            options.inJustDecodeBounds = false;
            bitmap = BitmapFactory.decodeStream(new FileInputStream(uri), null, options);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}
