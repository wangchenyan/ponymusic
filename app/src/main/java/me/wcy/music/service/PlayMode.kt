package me.wcy.music.service

/**
 * 播放模式
 * Created by wcy on 2015/12/26.
 */
sealed class PlayMode(val value: Int) {
    object Loop : PlayMode(0)
    object Shuffle : PlayMode(1)
    object Single : PlayMode(2)

    companion object {
        fun valueOf(value: Int): PlayMode {
            return when (value) {
                0 -> Loop
                1 -> Shuffle
                2 -> Single
                else -> Loop
            }
        }
    }
}