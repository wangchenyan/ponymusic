package me.wcy.music.discover

import me.wcy.common.net.NetResult
import me.wcy.common.net.gson.GsonConverterFactory
import me.wcy.common.utils.GsonUtils
import me.wcy.music.common.bean.LrcDataWrap
import me.wcy.music.common.bean.SongUrlData
import me.wcy.music.discover.recommend.bean.RecommendSongListData
import me.wcy.music.net.HttpClient
import me.wcy.music.storage.preference.ConfigPreferences
import retrofit2.Retrofit
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * Created by wangchenyan.top on 2023/9/6.
 */
interface DiscoverApi {

    @POST("recommend/songs")
    suspend fun getRecommendSongs(): NetResult<RecommendSongListData>

    @POST("song/url/v1")
    suspend fun getSongUrl(
        @Query("id") id: Long,
        @Query("level") level: String = "standard",
    ): NetResult<List<SongUrlData>>

    @POST("lyric")
    suspend fun getLrc(
        @Query("id") id: Long,
    ): LrcDataWrap

    companion object {
        private val api: DiscoverApi by lazy {
            val retrofit = Retrofit.Builder()
                .baseUrl(ConfigPreferences.apiDomain)
                .addConverterFactory(GsonConverterFactory.create(GsonUtils.gson, true))
                .client(HttpClient.okHttpClient)
                .build()
            retrofit.create(DiscoverApi::class.java)
        }

        fun get(): DiscoverApi = api
    }
}