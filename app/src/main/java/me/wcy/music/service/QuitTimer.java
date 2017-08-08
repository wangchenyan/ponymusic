package me.wcy.music.service;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.format.DateUtils;

import me.wcy.music.application.AppCache;

/**
 * Created by hzwangchenyan on 2017/8/8.
 */
public class QuitTimer {
    private PlayService mPlayService;
    private EventCallback<Long> mTimerCallback;
    private Handler mHandler;
    private long mTimerRemain;

    public static QuitTimer getInstance() {
        return SingletonHolder.sInstance;
    }

    private static class SingletonHolder {
        private static final QuitTimer sInstance = new QuitTimer();
    }

    private QuitTimer() {
    }

    public void init(@NonNull PlayService playService, @NonNull Handler handler, @NonNull EventCallback<Long> timerCallback) {
        mPlayService = playService;
        mHandler = handler;
        mTimerCallback = timerCallback;
    }

    public void start(long milli) {
        stop();
        if (milli > 0) {
            mTimerRemain = milli + DateUtils.SECOND_IN_MILLIS;
            mHandler.post(mQuitRunnable);
        } else {
            mTimerRemain = 0;
            mTimerCallback.onEvent(mTimerRemain);
        }
    }

    public void stop() {
        mHandler.removeCallbacks(mQuitRunnable);
    }

    private Runnable mQuitRunnable = new Runnable() {
        @Override
        public void run() {
            mTimerRemain -= DateUtils.SECOND_IN_MILLIS;
            if (mTimerRemain > 0) {
                mTimerCallback.onEvent(mTimerRemain);
                mHandler.postDelayed(this, DateUtils.SECOND_IN_MILLIS);
            } else {
                AppCache.clearStack();
                mPlayService.quit();
            }
        }
    };
}
