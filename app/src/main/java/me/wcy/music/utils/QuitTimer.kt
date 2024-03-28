package me.wcy.music.utils

import android.os.Handler
import android.os.Looper
import android.text.format.DateUtils

/**
 * Created by hzwangchenyan on 2017/8/8.
 */
class QuitTimer(private val listener: OnTimerListener) {
    private val handler: Handler by lazy {
        Handler(Looper.getMainLooper())
    }
    private var timerRemain: Long = 0

    fun start(milli: Long) {
        stop()
        if (milli > 0) {
            timerRemain = milli + DateUtils.SECOND_IN_MILLIS
            handler.post(mQuitRunnable)
        } else {
            timerRemain = 0
            listener.onTick(timerRemain)
        }
    }

    fun stop() {
        handler.removeCallbacks(mQuitRunnable)
    }

    private val mQuitRunnable: Runnable = object : Runnable {
        override fun run() {
            timerRemain -= DateUtils.SECOND_IN_MILLIS
            if (timerRemain > 0) {
                listener.onTick(timerRemain)
                handler.postDelayed(this, DateUtils.SECOND_IN_MILLIS)
            } else {
                listener.onTimeEnd()
            }
        }
    }

    interface OnTimerListener {
        /**
         * 更新定时停止播放时间
         */
        fun onTick(remain: Long)

        fun onTimeEnd()
    }
}