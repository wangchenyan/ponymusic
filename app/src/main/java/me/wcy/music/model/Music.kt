package me.wcy.music.model

import android.text.TextUtils
import java.io.Serializable

/**
 * 单曲信息
 * Created by wcy on 2015/11/27.
 */
class Music : Serializable {
    @JvmField
    var id: Long? = null

    @JvmField
    var type = 0 // 歌曲类型:本地/网络

    @JvmField
    var songId: Long = 0 // [本地]歌曲ID

    @JvmField
    var title: String? = null // 音乐标题

    @JvmField
    var artist: String? = null // 艺术家

    @JvmField
    var album: String? = null // 专辑

    @JvmField
    var albumId: Long = 0 // [本地]专辑ID

    @JvmField
    var coverPath: String? = null // [在线]专辑封面路径

    @JvmField
    var duration: Long = 0 // 持续时间

    @JvmField
    var path: String = "" // 播放地址

    @JvmField
    var fileName: String? = null // [本地]文件名

    @JvmField
    var fileSize: Long = 0 // [本地]文件大小

    interface Type {
        companion object {
            const val LOCAL = 0
            const val ONLINE = 1
        }
    }

    override fun equals(o: Any?): Boolean {
        if (o !is Music) {
            return false
        }
        val music = o
        if (music.songId > 0 && music.songId == songId) {
            return true
        }
        return (TextUtils.equals(music.title, title)
                && TextUtils.equals(music.artist, artist)
                && TextUtils.equals(music.album, album) && music.duration == duration)
    }

    companion object {
        private const val serialVersionUID: Long = 536871008
    }
}