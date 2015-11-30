package me.wcy.ponymusic.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.LruCache;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import me.wcy.ponymusic.R;
import me.wcy.ponymusic.application.MusicApplication;

/**
 * 专辑封面图片加载器
 * Created by wcy on 2015/11/27.
 */
public class CoverLoader {
    //缩略图LruCache，用于音乐列表
    private LruCache<String, Bitmap> mThumbnailCache;
    private LruCache<String, Bitmap> mNormalCache;
    private LruCache<String, Bitmap> mRoundCache;

    private CoverLoader() {
        int maxSize = (int) (Runtime.getRuntime().maxMemory() / 8);
        mThumbnailCache = new LruCache<String, Bitmap>(maxSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight();
            }
        };
        mNormalCache = new LruCache<String, Bitmap>(maxSize) {
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
        if (uri == null) {
            return null;
        }
        Bitmap bitmap = mNormalCache.get(uri);
        if (bitmap == null) {
            try {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true; // 仅获取大小
                bitmap = BitmapFactory.decodeStream(new FileInputStream(uri), null, options);
                //压缩尺寸，避免卡顿
                int inSampleSize = options.outHeight / 100;
                if (inSampleSize <= 1) {
                    inSampleSize = 1;
                }
                options.inSampleSize = inSampleSize;
                options.inJustDecodeBounds = false; // 获取bitmap
                bitmap = BitmapFactory.decodeStream(new FileInputStream(uri), null, options);
                mNormalCache.put(uri, bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    public Bitmap loadNormal(String uri) {
        if (uri == null) {
            return null;
        }
        Bitmap bitmap = mThumbnailCache.get(uri);
        if (bitmap == null) {
            bitmap = BitmapFactory.decodeFile(uri);
            bitmap = ImageUtils.boxBlurFilter(bitmap);
            mThumbnailCache.put(uri, bitmap);
        }
        return bitmap;
    }

    public Bitmap loadRound(String uri) {
        if (uri == null) {
            Bitmap bitmap = mRoundCache.get("null");
            if (bitmap == null) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.outWidth = MusicUtils.getScreenWidth() / 2;
                options.outHeight = MusicUtils.getScreenWidth() / 2;
                bitmap = BitmapFactory.decodeResource(MusicApplication.getInstance().getResources(), R.drawable.ic_play_page_default_cover, options);
                mRoundCache.put("null", bitmap);
            }
            return bitmap;
        }
        Bitmap bitmap = mRoundCache.get(uri);
        if (bitmap == null) {
            bitmap = BitmapFactory.decodeFile(uri);
            bitmap = ImageUtils.createCircleImage(bitmap);
            mRoundCache.put(uri, bitmap);
        }
        return bitmap;
    }
}
