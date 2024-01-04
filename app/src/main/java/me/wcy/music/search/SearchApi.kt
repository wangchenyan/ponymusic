package me.wcy.music.search

import top.wangchenyan.common.net.NetResult
import top.wangchenyan.common.net.gson.GsonConverterFactory
import top.wangchenyan.common.utils.GsonUtils
import me.wcy.music.net.HttpClient
import me.wcy.music.search.bean.SearchResultData
import me.wcy.music.storage.preference.ConfigPreferences
import retrofit2.Retrofit
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * Created by wangchenyan.top on 2023/9/20.
 */
interface SearchApi {

    /**
     * 搜索歌曲
     * @param type 搜索类型；默认为 1 即单曲 , 取值意义 :
     * - 1: 单曲,
     * - 10: 专辑,
     * - 100: 歌手,
     * - 1000: 歌单,
     * - 1002: 用户,
     * - 1004: MV,
     * - 1006: 歌词,
     * - 1009: 电台,
     * - 1014: 视频,
     * - 1018:综合,
     * - 2000:声音(搜索声音返回字段格式会不一样)
     */
    @POST("cloudsearch")
    suspend fun search(
        @Query("type") type: Int,
        @Query("keywords") keywords: String,
        @Query("limit") limit: Int,
        @Query("offset") offset: Int,
    ): NetResult<SearchResultData>

    companion object {
        private val api: SearchApi by lazy {
            val retrofit = Retrofit.Builder()
                .baseUrl(ConfigPreferences.apiDomain)
                .addConverterFactory(GsonConverterFactory.create(GsonUtils.gson, true))
                .client(HttpClient.okHttpClient)
                .build()
            retrofit.create(SearchApi::class.java)
        }

        fun get(): SearchApi = api
    }
}