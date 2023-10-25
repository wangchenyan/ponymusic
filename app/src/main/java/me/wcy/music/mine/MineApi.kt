package me.wcy.music.mine

import me.wcy.common.net.NetResult
import me.wcy.common.net.gson.GsonConverterFactory
import me.wcy.common.utils.GsonUtils
import me.wcy.common.utils.ServerTime
import me.wcy.music.discover.playlist.square.bean.PlaylistListData
import me.wcy.music.net.HttpClient
import me.wcy.music.storage.preference.ConfigPreferences
import retrofit2.Retrofit
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * Created by wangchenyan.top on 2023/9/26.
 */
interface MineApi {

    @POST("user/playlist")
    suspend fun getUserPlaylist(
        @Query("uid") uid: Long,
        @Query("limit") limit: Int = 1000,
        @Query("timestamp") timestamp: Long = ServerTime.currentTimeMillis()
    ): PlaylistListData

    /**
     * 收藏/取消收藏歌单
     * @param id 歌单 id
     * @param t 类型,1:收藏,2:取消收藏
     */
    @POST("playlist/subscribe")
    suspend fun collectPlaylist(
        @Query("id") id: Long,
        @Query("t") t: Int,
        @Query("timestamp") timestamp: Long = ServerTime.currentTimeMillis()
    ): NetResult<Any>

    companion object {
        private val api: MineApi by lazy {
            val retrofit = Retrofit.Builder()
                .baseUrl(ConfigPreferences.apiDomain)
                .addConverterFactory(GsonConverterFactory.create(GsonUtils.gson, true))
                .client(HttpClient.okHttpClient)
                .build()
            retrofit.create(MineApi::class.java)
        }

        fun get(): MineApi = api
    }
}