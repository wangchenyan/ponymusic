package me.wcy.music.utils

import android.content.Context
import android.os.Environment
import android.text.TextUtils
import com.blankj.utilcode.util.StringUtils
import me.wcy.music.R
import me.wcy.music.storage.db.entity.SongEntity
import java.io.File
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

    fun getLrcFileName(artist: String, title: String): String {
        return getFileName(artist, title) + LRC
    }

    fun getFileName(artist: String, title: String): String {
        var a = artist
        var t = title
        a = stringFilter(a)
        t = stringFilter(t)
        if (TextUtils.isEmpty(a)) {
            a = StringUtils.getString(R.string.unknown)
        }
        if (TextUtils.isEmpty(t)) {
            t = StringUtils.getString(R.string.unknown)
        }
        return "$a - $t"
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
    private fun stringFilter(str: String): String {
        val regEx = "[\\/:*?\"<>|]"
        val p = Pattern.compile(regEx)
        val m = p.matcher(str)
        return m.replaceAll("").trim { it <= ' ' }
    }
}