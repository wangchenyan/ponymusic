package me.wcy.ponymusic.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.LruCache;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * 专辑封面图片加载器
 * Created by wcy on 2015/11/27.
 */
public class CoverLoader {
    private LruCache<String, Bitmap> mCache;
    //缩略图LruCache，用于音乐列表
    private LruCache<String, Bitmap> mThumbnailCache;

    private CoverLoader() {
        int maxSize = (int) (Runtime.getRuntime().maxMemory() / 8);
        mCache = new LruCache<>(maxSize);
        mThumbnailCache = new LruCache<>(maxSize);
    }

    public static CoverLoader getInstance() {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private static CoverLoader instance = new CoverLoader();
    }

    public Bitmap load(String uri) {
        if (uri == null) {
            return null;
        }
        Bitmap bmp = mThumbnailCache.get(uri);
        if (bmp == null) {
            bmp = BitmapFactory.decodeFile(uri);
            mThumbnailCache.put(uri, bmp);
        }
        return bmp;
    }

    public Bitmap loadThumbnail(String uri) {
        if (uri == null) {
            return null;
        }
        Bitmap bmp = mCache.get(uri);
        if (bmp == null) {
            try {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true; // 仅获取大小
                bmp = BitmapFactory.decodeStream(new FileInputStream(uri), null, options);
                //压缩尺寸，避免卡顿
                int inSampleSize = options.outHeight / 100;
                if (inSampleSize <= 1) {
                    inSampleSize = 1;
                }
                options.inSampleSize = inSampleSize;
                options.inJustDecodeBounds = false; // 获取bitmap
                bmp = BitmapFactory.decodeStream(new FileInputStream(uri), null, options);
                mCache.put(uri, bmp);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return bmp;
    }
}
