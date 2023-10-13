package me.wcy.music

import android.animation.Animator

/**
 * Created by wangchenyan.top on 2023/10/18.
 */

fun Animator.startOrResume() {
    if (isPaused) {
        resume()
    } else {
        start()
    }
}