package me.wcy.music.net

import android.util.Log
import com.ihsanbal.logging.Level
import com.ihsanbal.logging.LoggingInterceptor
import top.wangchenyan.common.CommonApp
import top.wangchenyan.common.utils.ServerTime
import me.wcy.music.consts.FilePath
import okhttp3.Cache
import okhttp3.OkHttpClient
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * Created by wangchenyan.top on 2022/6/22.
 */
object HttpClient {

    val okHttpClient: OkHttpClient by lazy {
        val builder = OkHttpClient.Builder()
            .connectTimeout(125, TimeUnit.SECONDS)
            .readTimeout(125, TimeUnit.SECONDS)
            .writeTimeout(125, TimeUnit.SECONDS)
            // 忽略host验证
            .hostnameVerifier { hostname, session -> true }
            .cache(Cache(File(FilePath.httpCache), 10 * 1024 * 1024))
            .addInterceptor(HeaderInterceptor())
            .addInterceptor(ServerTime)
        if (CommonApp.test) {
            builder.addInterceptor(
                LoggingInterceptor.Builder()
                    .tag("MusicHttp")
                    .setLevel(Level.BASIC)
                    .log(Log.WARN)
                    .build()
            )
        }
        builder.build()
    }
}