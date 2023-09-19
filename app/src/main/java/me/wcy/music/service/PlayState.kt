package me.wcy.music.service

/**
 * Created by wangchenyan.top on 2023/9/18.
 */
sealed class PlayState {
    object Idle : PlayState()
    object Preparing : PlayState()
    object Playing : PlayState()
    object Pause : PlayState()

    val isIdle: Boolean
        get() = this is Idle
    val isPreparing: Boolean
        get() = this is Preparing
    val isPlaying: Boolean
        get() = this is Playing
    val isPausing: Boolean
        get() = this is Pause
}
