package me.wcy.music.utils.id3

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream
import java.io.File

/**
 * Created by hzwangchenyan on 2017/8/11.
 */
class ID3Tags {
    // 标题
    private var title: String? = null

    // 艺术家
    private var artist: String? = null

    // 专辑
    private var album: String? = null

    // 流派
    private var genre: String? = null

    // 年份
    private var year = 0

    // 注释
    private var comment: String? = null

    // 封面图片
    private var coverBitmap: Bitmap? = null

    class Builder {
        private val id3Tags: ID3Tags

        init {
            id3Tags = ID3Tags()
        }

        fun build(): ID3Tags {
            return id3Tags
        }

        fun setTitle(title: String?): Builder {
            id3Tags.title = title
            return this
        }

        fun setArtist(artist: String?): Builder {
            id3Tags.artist = artist
            return this
        }

        fun setAlbum(album: String?): Builder {
            id3Tags.album = album
            return this
        }

        fun setGenre(genre: String?): Builder {
            id3Tags.genre = genre
            return this
        }

        fun setYear(year: Int): Builder {
            id3Tags.year = year
            return this
        }

        fun setComment(comment: String?): Builder {
            id3Tags.comment = comment
            return this
        }

        fun setCoverFile(coverFile: File): Builder {
            val options = BitmapFactory.Options()
            options.inPreferredConfig = Bitmap.Config.RGB_565
            val coverBitmap = BitmapFactory.decodeFile(coverFile.path, options)
            return setCoverBitmap(coverBitmap)
        }

        fun setCoverBitmap(coverBitmap: Bitmap?): Builder {
            id3Tags.coverBitmap = coverBitmap
            return this
        }
    }

    private fun bitmapToBytes(bitmap: Bitmap): ByteArray {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        return baos.toByteArray()
    }

    companion object {
        private const val FRONT_COVER_DESC = "front_cover"
        private const val MIME_TYPE_JPEG = "image/jpeg"
    }
}