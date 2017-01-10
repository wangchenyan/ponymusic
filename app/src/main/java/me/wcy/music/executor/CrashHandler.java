package me.wcy.music.executor;

import android.util.Log;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import me.wcy.music.BuildConfig;
import me.wcy.music.utils.FileUtils;

/**
 * 异常捕获
 * Created by hzwangchenyan on 2016/1/25.
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {
    private static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private static SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault());

    private Thread.UncaughtExceptionHandler mDefaultHandler;

    public static CrashHandler getInstance() {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private static CrashHandler instance = new CrashHandler();
    }

    public void init() {
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!BuildConfig.DEBUG) {
            saveCrashInfo(ex);
        }
        mDefaultHandler.uncaughtException(thread, ex);
    }

    private void saveCrashInfo(Throwable ex) {
        String stackTrace = Log.getStackTraceString(ex);
        Date date = new Date();
        String dateStr = DATE_FORMAT.format(date);
        String filePath = FileUtils.getLogDir() + String.format("log_%s.log", dateStr);
        String time = TIME_FORMAT.format(date);
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, true));
            bw.write(String.format(Locale.getDefault(), "*** crash at %s *** ", time));
            bw.newLine();
            bw.write(stackTrace);
            bw.newLine();
            bw.flush();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
