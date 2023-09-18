package me.wcy.music.discover

import me.wcy.common.net.NetResult
import me.wcy.common.net.gson.GsonConverterFactory
import me.wcy.common.utils.GsonUtils
import me.wcy.music.common.bean.SongUrlData
import me.wcy.music.discover.recommend.bean.RecommendSongListData
import me.wcy.music.net.HttpClient
import me.wcy.music.storage.preference.MusicPreferences
import retrofit2.Retrofit
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * Created by wangchenyan.top on 2023/9/6.
 */
interface OnlineMusicApi {

    @POST("recommend/songs")
    suspend fun getRecommendSongs(): NetResult<RecommendSongListData>

    @POST("song/url/v1")
    suspend fun getSongUrl(
        @Query("id") id: Long,
        @Query("level") level: String = "standard",
    ): NetResult<List<SongUrlData>>

    companion object {
        private val api: OnlineMusicApi by lazy {
            val retrofit = Retrofit.Builder()
                .baseUrl(MusicPreferences.apiDomain)
                .addConverterFactory(GsonConverterFactory.create(GsonUtils.gson, true))
                .client(HttpClient.okHttpClient)
                .build()
            retrofit.create(OnlineMusicApi::class.java)
        }

        fun get(): OnlineMusicApi = api
    }
}