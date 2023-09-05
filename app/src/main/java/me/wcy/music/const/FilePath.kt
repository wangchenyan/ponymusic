package me.wcy.music.const

import me.wcy.common.CommonApp
import java.io.File

/**
 * Created by wangchenyan.top on 2022/9/24.
 */
object FilePath {
    val HTTP_CACHE: String
        get() = "http".assembleExternalCachePath()
    val APP_UPDATE: String
        get() = "update".assembleExternalCachePath()
    val LOG_ROOT_DIR: String
        get() = "log".assembleExternalFilePath()

    fun getLogPath(type: String): String {
        return LOG_ROOT_DIR + File.separator + type
    }

    private fun String.assembleExternalCachePath(): String {
        return "${CommonApp.app.externalCacheDir}${File.separator}music_$this"
    }

    private fun String.assembleExternalFilePath(): String {
        return CommonApp.app.getExternalFilesDir("music_$this")?.path ?: ""
    }
}