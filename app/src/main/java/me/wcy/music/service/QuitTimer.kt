package me.wcy.music.service

import android.os.Handler
import android.os.Looper
import android.text.format.DateUtils
import me.wcy.common.CommonApp
import me.wcy.music.application.AppCache
import me.wcy.music.constants.Actions

/**
 * Created by hzwangchenyan on 2017/8/8.
 */
class QuitTimer private constructor() {
    private var listener: OnTimerListener? = null
    private var handler: Handler? = null
    private var timerRemain: Long = 0

    interface OnTimerListener {
        /**
         * 更新定时停止播放时间
         */
        fun onTimer(remain: Long)
    }

    private object SingletonHolder {
        val sInstance = QuitTimer()
    }

    fun init() {
        handler = Handler(Looper.getMainLooper())
    }

    fun setOnTimerListener(listener: OnTimerListener?) {
        this.listener = listener
    }

    fun start(milli: Long) {
        stop()
        if (milli > 0) {
            timerRemain = milli + DateUtils.SECOND_IN_MILLIS
            handler!!.post(mQuitRunnable)
        } else {
            timerRemain = 0
            if (listener != null) {
                listener!!.onTimer(timerRemain)
            }
        }
    }

    fun stop() {
        handler!!.removeCallbacks(mQuitRunnable)
    }

    private val mQuitRunnable: Runnable = object : Runnable {
        override fun run() {
            timerRemain -= DateUtils.SECOND_IN_MILLIS
            if (timerRemain > 0) {
                if (listener != null) {
                    listener!!.onTimer(timerRemain)
                }
                handler!!.postDelayed(this, DateUtils.SECOND_IN_MILLIS)
            } else {
                AppCache.get().clearStack()
                PlayService.startCommand(CommonApp.app, Actions.ACTION_STOP)
            }
        }
    }

    companion object {
        fun get(): QuitTimer {
            return SingletonHolder.sInstance
        }
    }
}