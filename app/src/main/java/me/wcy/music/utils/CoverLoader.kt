package me.wcy.music.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.text.TextUtils
import androidx.collection.LruCache
import me.wcy.music.R
import me.wcy.music.model.Music
import java.io.FileNotFoundException
import java.io.InputStream

/**
 * 专辑封面图片加载器
 * Created by wcy on 2015/11/27.
 */
open class CoverLoader private constructor() {
    private var context: Context? = null
    private val cacheMap: MutableMap<Type, LruCache<String, Bitmap>> = HashMap(3)
    private var roundLength = ScreenUtils.screenWidth / 2

    private enum class Type {
        THUMB, ROUND, BLUR
    }

    private object SingletonHolder {
        val instance = CoverLoader()
    }

    fun init(context: Context?) {
        this.context = context!!.applicationContext

        // 获取当前进程的可用内存（单位KB）
        val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()
        // 缓存大小为当前进程可用内存的1/8
        val cacheSize = maxMemory / 8
        val thumbCache: LruCache<String, Bitmap> = object : LruCache<String, Bitmap>(cacheSize) {
            override fun sizeOf(key: String, bitmap: Bitmap): Int {
                return bitmap.allocationByteCount / 1024
            }
        }
        val roundCache = LruCache<String, Bitmap>(10)
        val blurCache = LruCache<String, Bitmap>(10)
        cacheMap[Type.THUMB] = thumbCache
        cacheMap[Type.ROUND] = roundCache
        cacheMap[Type.BLUR] = blurCache
    }

    fun setRoundLength(roundLength: Int) {
        if (this.roundLength != roundLength) {
            this.roundLength = roundLength
            cacheMap[Type.ROUND]!!.evictAll()
        }
    }

    fun loadThumb(music: Music?): Bitmap? {
        return loadCover(music, Type.THUMB)
    }

    fun loadRound(music: Music?): Bitmap? {
        return loadCover(music, Type.ROUND)
    }

    fun loadBlur(music: Music?): Bitmap? {
        return loadCover(music, Type.BLUR)
    }

    private fun loadCover(music: Music?, type: Type): Bitmap? {
        var bitmap: Bitmap?
        val key = getKey(music)
        val cache = cacheMap!![type]!!
        if (TextUtils.isEmpty(key)) {
            bitmap = cache[KEY_NULL]
            if (bitmap != null) {
                return bitmap
            }
            bitmap = getDefaultCover(type)
            cache.put(KEY_NULL, bitmap!!)
            return bitmap
        }
        bitmap = cache[key!!]
        if (bitmap != null) {
            return bitmap
        }
        bitmap = loadCoverByType(music, type)
        if (bitmap != null) {
            cache.put(key, bitmap)
            return bitmap
        }
        return loadCover(null, type)
    }

    private fun getKey(music: Music?): String? {
        if (music == null) {
            return null
        }
        return if (music.type == Music.Type.LOCAL && music.albumId > 0) {
            music.albumId.toString()
        } else if (music.type == Music.Type.ONLINE && !TextUtils.isEmpty(
                music.coverPath
            )
        ) {
            music.coverPath
        } else {
            null
        }
    }

    private fun getDefaultCover(type: Type): Bitmap? {
        return when (type) {
            Type.ROUND -> {
                var bitmap = BitmapFactory.decodeResource(
                    context!!.resources,
                    R.drawable.play_page_default_cover
                )
                bitmap = ImageUtils.resizeImage(bitmap, roundLength, roundLength)
                bitmap
            }

            Type.BLUR -> BitmapFactory.decodeResource(
                context!!.resources,
                R.drawable.play_page_default_bg
            )

            else -> BitmapFactory.decodeResource(context!!.resources, R.drawable.default_cover)
        }
    }

    private fun loadCoverByType(music: Music?, type: Type): Bitmap? {
        music ?: return null
        var bitmap: Bitmap?
        bitmap = if (music.type == Music.Type.LOCAL) {
            loadCoverFromMediaStore(music.albumId)
        } else {
            loadCoverFromFile(music.coverPath)
        }
        return when (type) {
            Type.ROUND -> {
                bitmap = ImageUtils.resizeImage(bitmap, roundLength, roundLength)
                ImageUtils.createCircleImage(bitmap)
            }

            Type.BLUR -> ImageUtils.blur(bitmap)
            else -> bitmap
        }
    }

    /**
     * 从媒体库加载封面<br></br>
     * 本地音乐
     */
    private fun loadCoverFromMediaStore(albumId: Long): Bitmap? {
        val resolver = context!!.contentResolver
        val uri = MusicUtils.getMediaStoreAlbumCoverUri(albumId)
        val `is`: InputStream?
        `is` = try {
            resolver.openInputStream(uri!!)
        } catch (ignored: FileNotFoundException) {
            return null
        }
        val options = BitmapFactory.Options()
        options.inPreferredConfig = Bitmap.Config.RGB_565
        return BitmapFactory.decodeStream(`is`, null, options)
    }

    /**
     * 从下载的图片加载封面<br></br>
     * 网络音乐
     */
    private fun loadCoverFromFile(path: String?): Bitmap {
        val options = BitmapFactory.Options()
        options.inPreferredConfig = Bitmap.Config.RGB_565
        return BitmapFactory.decodeFile(path, options)
    }

    companion object {
        const val THUMBNAIL_MAX_LENGTH = 500
        private const val KEY_NULL = "null"
        fun get(): CoverLoader {
            return SingletonHolder.instance
        }
    }
}