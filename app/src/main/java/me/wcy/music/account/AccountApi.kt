package me.wcy.music.account

import me.wcy.common.net.NetResult
import me.wcy.common.net.gson.GsonConverterFactory
import me.wcy.common.utils.GsonUtils
import me.wcy.common.utils.ServerTime
import me.wcy.music.account.bean.CheckLoginStatusData
import me.wcy.music.account.bean.LoginStatusData
import me.wcy.music.account.bean.QrCodeData
import me.wcy.music.account.bean.QrCodeKeyData
import me.wcy.music.net.HttpClient
import me.wcy.music.storage.preference.ConfigPreferences
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * Created by wangchenyan.top on 2023/8/25.
 */
interface AccountApi {

    @GET("login/qr/key")
    suspend fun getQrCodeKey(
        @Query("timestamp") timestamp: Long = ServerTime.currentTimeMillis()
    ): NetResult<QrCodeKeyData>

    @GET("login/qr/create")
    suspend fun getLoginQrCode(
        @Query("key") key: String,
        @Query("timestamp") timestamp: Long = ServerTime.currentTimeMillis()
    ): NetResult<QrCodeData>

    @GET("login/qr/check")
    suspend fun checkLoginStatus(
        @Query("key") key: String,
        @Query("timestamp") timestamp: Long = ServerTime.currentTimeMillis(),
        @Query("noCookie") noCookie: Boolean = true
    ): CheckLoginStatusData

    @POST("login/status")
    suspend fun getLoginStatus(
        @Query("timestamp") timestamp: Long = ServerTime.currentTimeMillis()
    ): LoginStatusData

    companion object {
        private val api: AccountApi by lazy {
            val retrofit = Retrofit.Builder()
                .baseUrl(ConfigPreferences.apiDomain)
                .addConverterFactory(GsonConverterFactory.create(GsonUtils.gson, true))
                .client(HttpClient.okHttpClient)
                .build()
            retrofit.create(AccountApi::class.java)
        }

        fun get(): AccountApi = api
    }
}