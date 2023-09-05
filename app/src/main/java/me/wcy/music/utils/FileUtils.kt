package me.wcy.music.utils

import android.content.Context
import android.os.Environment
import android.text.TextUtils
import me.wcy.music.R
import me.wcy.music.common.AppCache
import me.wcy.music.model.Music
import me.wcy.music.storage.db.entity.SongEntity
import java.io.File
import java.util.Locale
import java.util.regex.Pattern

/**
 * 文件工具类
 * Created by wcy on 2016/1/3.
 */
object FileUtils {
    private const val MP3 = ".mp3"
    private const val LRC = ".lrc"

    private val appDir = Environment.getExternalStorageDirectory().toString() + "/PonyMusic"

    fun getMusicDir(): String {
        val dir = "$appDir/Music"
        return mkdirs(dir)
    }

    fun getLrcDir(): String {
        val dir = "$appDir/Lyric"
        return mkdirs(dir)
    }

    fun getRelativeMusicDir(): String {
        val dir = "PonyMusic/Music"
        return mkdirs(dir)
    }

    fun getCorpImagePath(context: Context): String {
        return context.externalCacheDir.toString() + "/corp.jpg"
    }

    /**
     * 获取歌词路径<br></br>
     * 先从已下载文件夹中查找，如果不存在，则从歌曲文件所在文件夹查找。
     *
     * @return 如果存在返回路径，否则返回null
     */
    fun getLrcFilePath(music: Music?): String? {
        if (music == null) {
            return null
        }
        var lrcFilePath: String? = getLrcDir() + "/" + getLrcFileName(music.artist, music.title)
        if (!exists(lrcFilePath)) {
            lrcFilePath = music.path.replace(MP3, LRC)
            if (!exists(lrcFilePath)) {
                lrcFilePath = null
            }
        }
        return lrcFilePath
    }

    /**
     * 获取歌词路径<br></br>
     * 先从已下载文件夹中查找，如果不存在，则从歌曲文件所在文件夹查找。
     *
     * @return 如果存在返回路径，否则返回null
     */
    fun getLrcFilePath(music: SongEntity): String? {
        var lrcFilePath: String? = getLrcDir() + "/" + getLrcFileName(music.artist, music.title)
        if (!exists(lrcFilePath)) {
            lrcFilePath = music.path.replace(MP3, LRC)
            if (!exists(lrcFilePath)) {
                lrcFilePath = null
            }
        }
        return lrcFilePath
    }

    private fun mkdirs(dir: String): String {
        val file = File(dir)
        if (!file.exists()) {
            file.mkdirs()
        }
        return dir
    }

    private fun exists(path: String?): Boolean {
        val file = File(path)
        return file.exists()
    }

    fun getMp3FileName(artist: String?, title: String?): String {
        return getFileName(artist, title) + MP3
    }

    fun getLrcFileName(artist: String?, title: String?): String {
        return getFileName(artist, title) + LRC
    }

    fun getFileName(artist: String?, title: String?): String {
        var artist = artist
        var title = title
        artist = stringFilter(artist)
        title = stringFilter(title)
        if (TextUtils.isEmpty(artist)) {
            artist = AppCache.get().getContext()!!.getString(R.string.unknown)
        }
        if (TextUtils.isEmpty(title)) {
            title = AppCache.get().getContext()!!.getString(R.string.unknown)
        }
        return "$artist - $title"
    }

    fun getArtistAndAlbum(artist: String?, album: String?): String? {
        return if (TextUtils.isEmpty(artist) && TextUtils.isEmpty(album)) {
            ""
        } else if (!TextUtils.isEmpty(artist) && TextUtils.isEmpty(album)) {
            artist
        } else if (TextUtils.isEmpty(artist) && !TextUtils.isEmpty(album)) {
            album
        } else {
            "$artist - $album"
        }
    }

    /**
     * 过滤特殊字符(\/:*?"<>|)
     */
    private fun stringFilter(str: String?): String? {
        if (str == null) {
            return null
        }
        val regEx = "[\\/:*?\"<>|]"
        val p = Pattern.compile(regEx)
        val m = p.matcher(str)
        return m.replaceAll("").trim { it <= ' ' }
    }

    fun b2mb(b: Int): Float {
        val mb = String.format(Locale.getDefault(), "%.2f", b.toFloat() / 1024 / 1024)
        return java.lang.Float.valueOf(mb)
    }
}