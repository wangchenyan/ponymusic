package me.wcy.music.application;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by hzwangchenyan on 2017/9/20.
 */
public class ForegroundObserver implements Application.ActivityLifecycleCallbacks {
    private static final String TAG = "ForegroundObserver";
    private static final long CHECK_TASK_DELAY = 500;

    private List<Observer> observerList;
    private Handler handler;
    private boolean isForeground;
    private int resumeActivityCount;

    public interface Observer {
        /**
         * 进入前台
         *
         * @param activity 当前处于栈顶的Activity
         */
        void onForeground(Activity activity);

        /**
         * 进入后台
         *
         * @param activity 当前处于栈顶的Activity
         */
        void onBackground(Activity activity);
    }

    public static void init(Application application) {
        application.registerActivityLifecycleCallbacks(getInstance());
    }

    private static ForegroundObserver getInstance() {
        return SingletonHolder.sInstance;
    }

    private static class SingletonHolder {
        private static ForegroundObserver sInstance = new ForegroundObserver();
    }

    private ForegroundObserver() {
        observerList = Collections.synchronizedList(new ArrayList<Observer>());
        handler = new Handler(Looper.getMainLooper());
    }

    public static void addObserver(Observer observer) {
        if (observer == null) {
            return;
        }

        if (getInstance().observerList.contains(observer)) {
            return;
        }

        getInstance().observerList.add(observer);
    }

    public static void removeObserver(Observer observer) {
        if (observer == null) {
            return;
        }

        getInstance().observerList.remove(observer);
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    }

    @Override
    public void onActivityStarted(Activity activity) {
    }

    @Override
    public void onActivityResumed(Activity activity) {
        resumeActivityCount++;
        if (!isForeground && resumeActivityCount > 0) {
            isForeground = true;
            // 从后台进入前台
            Log.i(TAG, "app in foreground");
            notify(activity, true);
        }
    }

    @Override
    public void onActivityPaused(final Activity activity) {
        resumeActivityCount--;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isForeground && resumeActivityCount == 0) {
                    isForeground = false;
                    // 从前台进入后台
                    Log.i(TAG, "app in background");
                    ForegroundObserver.this.notify(activity, false);
                }
            }
        }, CHECK_TASK_DELAY);
    }

    private void notify(Activity activity, boolean foreground) {
        for (Observer observer : observerList) {
            if (foreground) {
                observer.onForeground(activity);
            } else {
                observer.onBackground(activity);
            }
        }
    }

    @Override
    public void onActivityStopped(Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
    }
}
