package me.wcy.music.storage

import androidx.media3.common.MediaItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.wcy.music.consts.FilePath
import me.wcy.music.utils.getFilePath
import me.wcy.music.utils.getSongId
import me.wcy.music.utils.isLocal
import java.io.File

/**
 * Created by wangchenyan.top on 2023/9/18.
 */
object LrcCache {

    /**
     * 获取歌词路径
     */
    fun getLrcFilePath(music: MediaItem): String? {
        if (music.isLocal()) {
            val audioFile = File(music.mediaMetadata.getFilePath())
            val lrcFile = File(audioFile.parent, "${audioFile.nameWithoutExtension}.lrc")
            if (lrcFile.exists()) {
                return lrcFile.path
            }
        } else {
            val lrcFile = File(FilePath.lrcDir, music.getSongId().toString())
            if (lrcFile.exists()) {
                return lrcFile.path
            }
        }
        return null
    }

    suspend fun saveLrcFile(music: MediaItem, content: String): File {
        return withContext(Dispatchers.IO) {
            File(FilePath.lrcDir, music.getSongId().toString()).also {
                it.writeText(content)
            }
        }
    }
}