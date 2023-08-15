package me.wcy.music.utils

/**
 * Created by wcy on 2017/7/8.
 */
object ParseUtils {
    fun parseInt(s: String): Long {
        return try {
            s.toInt().toLong()
        } catch (e: NumberFormatException) {
            0
        }
    }

    fun parseLong(s: String?): Long {
        return try {
            s!!.toLong()
        } catch (e: NumberFormatException) {
            0
        }
    }
}