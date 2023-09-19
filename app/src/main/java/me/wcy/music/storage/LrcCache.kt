package me.wcy.music.storage

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.wcy.music.consts.FilePath
import me.wcy.music.storage.db.entity.SongEntity
import java.io.File

/**
 * Created by wangchenyan.top on 2023/9/18.
 */
object LrcCache {

    /**
     * 获取歌词路径
     */
    fun getLrcFilePath(music: SongEntity): String? {
        if (music.isLocal()) {
            val audioFile = File(music.path)
            val lrcFile = File(audioFile.parent, "${audioFile.nameWithoutExtension}.lrc")
            if (lrcFile.exists()) {
                return lrcFile.path
            }
        } else {
            val lrcFile = File(FilePath.lrcDir, music.songId.toString())
            if (lrcFile.exists()) {
                return lrcFile.path
            }
        }
        return null
    }

    suspend fun saveLrcFile(music: SongEntity, content: String): File {
        return withContext(Dispatchers.IO) {
            File(FilePath.lrcDir, music.songId.toString()).also {
                it.writeText(content)
            }
        }
    }
}