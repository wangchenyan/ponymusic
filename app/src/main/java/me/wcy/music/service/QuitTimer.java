package me.wcy.music.service;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.text.format.DateUtils;

import me.wcy.music.application.AppCache;

/**
 * Created by hzwangchenyan on 2017/8/8.
 */
public class QuitTimer {
    private PlayService playService;
    private OnTimerListener listener;
    private Handler handler;
    private long timerRemain;

    public interface OnTimerListener {
        /**
         * 更新定时停止播放时间
         */
        void onTimer(long remain);
    }

    public static QuitTimer get() {
        return SingletonHolder.sInstance;
    }

    private static class SingletonHolder {
        private static final QuitTimer sInstance = new QuitTimer();
    }

    private QuitTimer() {
    }

    public void init(@NonNull PlayService playService) {
        this.playService = playService;
        this.handler = new Handler(Looper.getMainLooper());
    }

    public void setOnTimerListener(OnTimerListener listener) {
        this.listener = listener;
    }

    public void start(long milli) {
        stop();
        if (milli > 0) {
            timerRemain = milli + DateUtils.SECOND_IN_MILLIS;
            handler.post(mQuitRunnable);
        } else {
            timerRemain = 0;
            if (listener != null) {
                listener.onTimer(timerRemain);
            }
        }
    }

    public void stop() {
        handler.removeCallbacks(mQuitRunnable);
    }

    private Runnable mQuitRunnable = new Runnable() {
        @Override
        public void run() {
            timerRemain -= DateUtils.SECOND_IN_MILLIS;
            if (timerRemain > 0) {
                if (listener != null) {
                    listener.onTimer(timerRemain);
                }
                handler.postDelayed(this, DateUtils.SECOND_IN_MILLIS);
            } else {
                AppCache.get().clearStack();
                playService.stop();
            }
        }
    };
}
