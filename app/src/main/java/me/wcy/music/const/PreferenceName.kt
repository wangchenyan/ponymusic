package me.wcy.music.const

/**
 * Created by wangchenyan.top on 2023/4/19.
 */
object PreferenceName {
    val ACCOUNT = "account".assemble()
    val CONFIG = "config".assemble()

    private fun String.assemble(): String {
        return "music_$this"
    }
}