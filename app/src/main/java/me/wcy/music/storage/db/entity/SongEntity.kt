package me.wcy.music.storage.db.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import me.wcy.music.utils.MusicUtils.asLargeCover
import me.wcy.music.utils.MusicUtils.asSmallCover
import me.wcy.music.utils.generateUniqueId

/**
 * Created by wangchenyan.top on 2023/8/29.
 */
@Parcelize
@Entity("play_list", indices = [Index("title"), Index("artist"), Index("album")])
data class SongEntity(
    // 歌曲类型:本地/网络
    @ColumnInfo("type")
    val type: Int = 0,

    // 歌曲ID
    @ColumnInfo("song_id")
    val songId: Long = 0,

    // 音乐标题
    @ColumnInfo("title")
    val title: String = "",

    // 艺术家
    @ColumnInfo("artist")
    val artist: String = "",

    // 艺术家ID
    @ColumnInfo("artist_id")
    val artistId: Long = 0,

    // 专辑
    @ColumnInfo("album")
    val album: String = "",

    // 专辑ID
    @ColumnInfo("album_id")
    val albumId: Long = 0,

    // 专辑封面
    @Deprecated("Please use resized url")
    @ColumnInfo("album_cover")
    val albumCover: String = "",

    // 持续时间
    @ColumnInfo("duration")
    val duration: Long = 0,

    // 播放地址
    @ColumnInfo("uri", defaultValue = "")
    var uri: String = "",

    // [本地]文件路径
    @ColumnInfo("path")
    var path: String = "",

    // [本地]文件名
    @ColumnInfo("file_name")
    val fileName: String = "",

    // [本地]文件大小
    @ColumnInfo("file_size")
    val fileSize: Long = 0,
) : Parcelable {
    @PrimaryKey
    @ColumnInfo("unique_id")
    var uniqueId: String = generateUniqueId(type, songId)

    override fun hashCode(): Int {
        return uniqueId.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return other is SongEntity
                && other.uniqueId == this.uniqueId
    }

    fun isLocal() = type == LOCAL

    fun getSmallCover(): String {
        if (isLocal()) return albumCover
        return albumCover.asSmallCover()
    }

    fun getLargeCover(): String {
        if (isLocal()) return albumCover
        return albumCover.asLargeCover()
    }

    companion object {
        const val LOCAL = 0
        const val ONLINE = 1
    }
}
