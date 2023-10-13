package me.wcy.music.service

import androidx.annotation.StringRes
import me.wcy.music.R

/**
 * 播放模式
 * Created by wcy on 2015/12/26.
 */
sealed class PlayMode(val value: Int, @StringRes val nameRes: Int) {
    object Loop : PlayMode(0, R.string.play_mode_loop)
    object Shuffle : PlayMode(1, R.string.play_mode_shuffle)
    object Single : PlayMode(2, R.string.play_mode_single)

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