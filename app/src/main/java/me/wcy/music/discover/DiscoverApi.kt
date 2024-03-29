package me.wcy.music.discover

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.wcy.music.common.bean.LrcDataWrap
import me.wcy.music.common.bean.SongData
import me.wcy.music.common.bean.SongUrlData
import me.wcy.music.discover.banner.BannerListData
import me.wcy.music.discover.playlist.detail.bean.PlaylistDetailData
import me.wcy.music.discover.playlist.detail.bean.SongListData
import me.wcy.music.discover.playlist.square.bean.PlaylistListData
import me.wcy.music.discover.playlist.square.bean.PlaylistTagListData
import me.wcy.music.discover.recommend.song.bean.RecommendSongListData
import me.wcy.music.net.HttpClient
import me.wcy.music.storage.preference.ConfigPreferences
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import top.wangchenyan.common.net.NetResult
import top.wangchenyan.common.net.gson.GsonConverterFactory
import top.wangchenyan.common.utils.GsonUtils

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
        @Query("level") level: String,
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
        @Query("limit") limit: Int? = null,
        @Query("offset") offset: Int? = null,
        @Query("timestamp") timestamp: Long? = null
    ): SongListData

    @POST("playlist/hot")
    suspend fun getPlaylistTagList(): PlaylistTagListData

    @POST("top/playlist")
    suspend fun getPlaylistList(
        @Query("cat") cat: String,
        @Query("limit") limit: Int,
        @Query("offset") offset: Int,
    ): PlaylistListData

    @POST("toplist")
    suspend fun getRankingList(): PlaylistListData

    @GET("banner?type=2")
    suspend fun getBannerList(): BannerListData

    companion object {
        private const val SONG_LIST_LIMIT = 800

        private val api: DiscoverApi by lazy {
            val retrofit = Retrofit.Builder()
                .baseUrl(ConfigPreferences.apiDomain)
                .addConverterFactory(GsonConverterFactory.create(GsonUtils.gson, true))
                .client(HttpClient.okHttpClient)
                .build()
            retrofit.create(DiscoverApi::class.java)
        }

        fun get(): DiscoverApi = api

        suspend fun getFullPlaylistSongList(id: Long, timestamp: Long? = null): SongListData {
            return withContext(Dispatchers.IO) {
                var offset = 0
                val list = mutableListOf<SongData>()
                while (true) {
                    val songList = get().getPlaylistSongList(
                        id,
                        limit = SONG_LIST_LIMIT,
                        offset = offset,
                        timestamp = timestamp
                    )
                    if (songList.code != 200) {
                        throw Exception("code = ${songList.code}")
                    }
                    if (songList.songs.isEmpty()) {
                        break
                    }
                    list.addAll(songList.songs)
                    offset = list.size
                }
                return@withContext SongListData(200, list)
            }
        }
    }
}