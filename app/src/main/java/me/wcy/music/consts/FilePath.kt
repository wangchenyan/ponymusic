package me.wcy.music.consts

import top.wangchenyan.common.CommonApp
import java.io.File

/**
 * Created by wangchenyan.top on 2022/9/24.
 */
object FilePath {
    val httpCache: String
        get() = "http".assembleExternalCachePath()
    val logRootDir: String
        get() = "log".assembleExternalFilePath()
    val lrcDir: String
        get() = "lrc".assembleExternalFilePath().mkdirs()

    fun getLogPath(type: String): String {
        return logRootDir + File.separator + type
    }

    private fun String.assembleExternalCachePath(): String {
        return "${CommonApp.app.externalCacheDir}${File.separator}music_$this"
    }

    private fun String.assembleExternalFilePath(): String {
        return CommonApp.app.getExternalFilesDir("music_$this")?.path ?: ""
    }

    private fun String.mkdirs() = apply {
        val file = File(this)
        if (!file.exists()) {
            file.mkdirs()
        }
    }
}