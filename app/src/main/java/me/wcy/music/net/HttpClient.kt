package me.wcy.music.net

import android.util.Log
import com.franmontiel.persistentcookiejar.PersistentCookieJar
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import com.ihsanbal.logging.Level
import com.ihsanbal.logging.LoggingInterceptor
import me.wcy.common.CommonApp
import me.wcy.music.const.FilePath
import okhttp3.Cache
import okhttp3.OkHttpClient
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * Created by wangchenyan.top on 2022/6/22.
 */
object HttpClient {
    private val cookieJar by lazy {
        PersistentCookieJar(
            SetCookieCache(),
            SharedPrefsCookiePersistor(CommonApp.app)
        )
    }

    val okHttpClient: OkHttpClient by lazy {
        val builder = OkHttpClient.Builder()
            .connectTimeout(125, TimeUnit.SECONDS)
            .readTimeout(125, TimeUnit.SECONDS)
            .writeTimeout(125, TimeUnit.SECONDS)
            // 忽略host验证
            .hostnameVerifier { hostname, session -> true }
            //.cookieJar(cookieJar)
            .cache(Cache(File(FilePath.HTTP_CACHE), 10 * 1024 * 1024))
            .addInterceptor(HeaderInterceptor())
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

    fun clearCookie() {
        cookieJar.clear()
    }
}