package me.wcy.music.application

import android.os.Build
import android.util.Log
import me.wcy.music.BuildConfig
import me.wcy.music.utils.FileUtils
import java.io.BufferedWriter
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 异常捕获
 * Created by hzwangchenyan on 2016/1/25.
 */
class CrashHandler : Thread.UncaughtExceptionHandler {
    private var mDefaultHandler: Thread.UncaughtExceptionHandler? = null

    private object SingletonHolder {
        val instance = CrashHandler()
    }

    fun init() {
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler(this)
    }

    override fun uncaughtException(thread: Thread, ex: Throwable) {
        saveCrashInfo(ex)
        mDefaultHandler!!.uncaughtException(thread, ex)
    }

    private fun saveCrashInfo(ex: Throwable) {
        val stackTrace = Log.getStackTraceString(ex)
        val filePath = FileUtils.logDir + "crash.log"
        try {
            val bw = BufferedWriter(FileWriter(filePath, true))
            bw.write("*** crash ***\n")
            bw.write(getInfo())
            bw.write(stackTrace)
            bw.newLine()
            bw.newLine()
            bw.flush()
            bw.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun getInfo(): String {
        val time = TIME_FORMAT.format(Date())
        val version = BuildConfig.VERSION_NAME + "/" + BuildConfig.VERSION_CODE
        val device = Build.MANUFACTURER + "/" + Build.MODEL + "/" + Build.VERSION.RELEASE
        val sb = StringBuilder()
        sb.append("*** time: ").append(time).append(" ***").append("\n")
        sb.append("*** version: ").append(version).append(" ***").append("\n")
        sb.append("*** device: ").append(device).append(" ***").append("\n")
        return sb.toString()
    }

    companion object {
        private val TIME_FORMAT = SimpleDateFormat("MM-dd HH:mm:ss.SSS", Locale.getDefault())
        fun getInstance(): CrashHandler {
            return SingletonHolder.instance
        }
    }
}