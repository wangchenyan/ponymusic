package me.wcy.music.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.util.LruCache;
import android.text.TextUtils;

import me.wcy.music.R;
import me.wcy.music.application.AppCache;

/**
 * 专辑封面图片加载器
 * Created by wcy on 2015/11/27.
 */
public class CoverLoader {
    private static final String KEY_NULL = "null";
    /**
     * 缩略图缓存，用于音乐列表
     */
    private LruCache<String, Bitmap> mThumbnailCache;
    /**
     * 高斯模糊图缓存，用于播放页背景
     */
    private LruCache<String, Bitmap> mBlurCache;
    /**
     * 圆形图缓存，用于播放页CD
     */
    private LruCache<String, Bitmap> mRoundCache;

    private CoverLoader() {
        // 获取当前进程的可用内存（单位KB）
        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        // 缓存大小为当前进程可用内存的1/8
        int cacheSize = maxMemory / 8;
        mThumbnailCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // 转换为KB，以达到与cacheSize的单位统一
                return bitmap.getByteCount() / 1024;
            }
        };
        mBlurCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount() / 1024;
            }
        };
        mRoundCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount() / 1024;
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
                bitmap = BitmapFactory.decodeResource(AppCache.getContext().getResources(), R.drawable.default_cover);
                mThumbnailCache.put(KEY_NULL, bitmap);
            }
        } else {
            bitmap = mThumbnailCache.get(uri);
            if (bitmap == null) {
                bitmap = loadBitmap(uri, ScreenUtils.getScreenWidth() / 10);
                if (bitmap == null) {
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
                bitmap = BitmapFactory.decodeResource(AppCache.getContext().getResources(), R.drawable.play_page_default_bg);
                mBlurCache.put(KEY_NULL, bitmap);
            }
        } else {
            bitmap = mBlurCache.get(uri);
            if (bitmap == null) {
                bitmap = loadBitmap(uri, ScreenUtils.getScreenWidth() / 2);
                if (bitmap == null) {
                    bitmap = loadBlur(null);
                } else {
                    bitmap = ImageUtils.blur(bitmap, ImageUtils.BLUR_RADIUS);
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
                bitmap = BitmapFactory.decodeResource(AppCache.getContext().getResources(), R.drawable.play_page_default_cover);
                bitmap = ImageUtils.resizeImage(bitmap, ScreenUtils.getScreenWidth() / 2, ScreenUtils.getScreenWidth() / 2);
                mRoundCache.put(KEY_NULL, bitmap);
            }
        } else {
            bitmap = mRoundCache.get(uri);
            if (bitmap == null) {
                bitmap = loadBitmap(uri, ScreenUtils.getScreenWidth() / 2);
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
     * 获得指定大小的bitmap
     */
    private Bitmap loadBitmap(String uri, int length) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        // 仅获取大小
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(uri, options);
        int maxLength = options.outWidth > options.outHeight ? options.outWidth : options.outHeight;
        // 压缩尺寸，避免卡顿
        int inSampleSize = maxLength / length;
        if (inSampleSize < 1) {
            inSampleSize = 1;
        }
        options.inSampleSize = inSampleSize;
        // 获取bitmap
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(uri, options);
    }
}
