package me.wcy.music.discover

import me.wcy.common.net.NetResult
import me.wcy.common.net.gson.GsonConverterFactory
import me.wcy.common.utils.GsonUtils
import me.wcy.music.common.bean.LrcDataWrap
import me.wcy.music.common.bean.SongUrlData
import me.wcy.music.discover.playlist.detail.bean.PlaylistDetailData
import me.wcy.music.discover.playlist.detail.bean.PlaylistSongListData
import me.wcy.music.discover.playlist.square.bean.PlaylistListData
import me.wcy.music.discover.playlist.square.bean.PlaylistTagListData
import me.wcy.music.discover.recommend.song.bean.RecommendSongListData
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

    @POST("recommend/resource")
    suspend fun getRecommendPlaylists(): PlaylistListData

    @POST("song/url/v1")
    suspend fun getSongUrl(
        @Query("id") id: Long,
        @Query("level") level: String = "standard",
    ): NetResult<List<SongUrlData>>

    @POST("lyric")
    suspend fun getLrc(
        @Query("id") id: Long,
    ): LrcDataWrap

    @POST("playlist/detail")
    suspend fun getPlaylistDetail(
        @Query("id") id: Long,
    ): PlaylistDetailData

    @POST("playlist/track/all")
    suspend fun getPlaylistSongList(
        @Query("id") id: Long,
    ): PlaylistSongListData

    @POST("playlist/hot")
    suspend fun getPlaylistTagList(): PlaylistTagListData

    @POST("top/playlist")
    suspend fun getPlaylistList(
        @Query("cat") cat: String,
        @Query("limit") limit: Int,
        @Query("offset") offset: Int,
    ): PlaylistListData

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