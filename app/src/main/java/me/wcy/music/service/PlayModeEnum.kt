package me.wcy.music.service

/**
 * 播放模式
 * Created by wcy on 2015/12/26.
 */
enum class PlayModeEnum(private val value: Int) {
    LOOP(0),
    SHUFFLE(1),
    SINGLE(2);

    fun value(): Int {
        return value
    }

    companion object {
        fun valueOf(value: Int): PlayModeEnum {
            return when (value) {
                1 -> SHUFFLE
                2 -> SINGLE
                0 -> LOOP
                else -> LOOP
            }
        }
    }
}