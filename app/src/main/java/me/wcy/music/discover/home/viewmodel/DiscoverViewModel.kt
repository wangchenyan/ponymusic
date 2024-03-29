package me.wcy.music.discover.home.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import me.wcy.music.account.service.UserService
import me.wcy.music.common.bean.PlaylistData
import me.wcy.music.discover.DiscoverApi
import me.wcy.music.discover.banner.BannerData
import me.wcy.music.net.NetCache
import me.wcy.music.storage.preference.ConfigPreferences
import top.wangchenyan.common.ext.toUnMutable
import javax.inject.Inject

/**
 * Created by wangchenyan.top on 2023/9/25.
 */
@HiltViewModel
class DiscoverViewModel @Inject constructor(
    private val userService: UserService
) : ViewModel() {
    private val _bannerList = MutableStateFlow<List<BannerData>>(emptyList())
    val bannerList = _bannerList.toUnMutable()

    private val _recommendPlaylist = MutableStateFlow<List<PlaylistData>>(emptyList())
    val recommendPlaylist = _recommendPlaylist.toUnMutable()

    private val _rankingList = MutableLiveData<List<PlaylistData>>(emptyList())
    val rankingList = _rankingList.toUnMutable()

    init {
        loadCache()
        viewModelScope.launch {
            userService.profile.collectLatest { profile ->
                if (profile != null && ConfigPreferences.apiDomain.isNotEmpty()) {
                    loadRecommendPlaylist()
                }
            }
        }
        loadBanner()
        loadRankingList()
    }

    private fun loadCache() {
        viewModelScope.launch {
            val list = NetCache.globalCache.getJsonArray(CACHE_KEY_BANNER, BannerData::class.java)
                ?: return@launch
            _bannerList.value = list
        }
        if (userService.isLogin()) {
            viewModelScope.launch {
                val list = NetCache.userCache.getJsonArray(
                    CACHE_KEY_REC_PLAYLIST,
                    PlaylistData::class.java
                ) ?: return@launch
                _recommendPlaylist.value = list
            }
        }
        viewModelScope.launch {
            val list =
                NetCache.globalCache.getJsonArray(CACHE_KEY_RANKING_LIST, PlaylistData::class.java)
                    ?: return@launch
            _rankingList.postValue(list)
        }
    }

    private fun loadBanner() {
        viewModelScope.launch {
            kotlin.runCatching {
                DiscoverApi.get().getBannerList()
            }.onSuccess {
                _bannerList.value = it.banners
                NetCache.globalCache.putJson(CACHE_KEY_BANNER, it.banners)
            }.onFailure {
            }
        }
    }

    private fun loadRecommendPlaylist() {
        viewModelScope.launch {
            kotlin.runCatching {
                DiscoverApi.get().getRecommendPlaylists()
            }.onSuccess {
                _recommendPlaylist.value = it.playlists
                NetCache.userCache.putJson(CACHE_KEY_REC_PLAYLIST, it.playlists)
            }.onFailure {
            }
        }
    }

    private fun loadRankingList() {
        viewModelScope.launch {
            kotlin.runCatching {
                DiscoverApi.get().getRankingList()
            }.onSuccess {
                val rankingList = it.playlists.take(5)
                val deferredList = mutableListOf<Deferred<*>>()
                rankingList.forEach {
                    val d = async {
                        val songListRes = kotlin.runCatching {
                            DiscoverApi.get().getPlaylistSongList(it.id, limit = 3)
                        }
                        if (songListRes.getOrNull()?.code == 200) {
                            it.songList = songListRes.getOrThrow().songs
                        }
                    }
                    deferredList.add(d)
                }
                deferredList.forEach { d ->
                    d.await()
                }
                _rankingList.postValue(rankingList)
                NetCache.globalCache.putJson(CACHE_KEY_RANKING_LIST, rankingList)
            }.onFailure {
            }
        }
    }

    companion object {
        const val CACHE_KEY_BANNER = "discover_banner"
        const val CACHE_KEY_REC_PLAYLIST = "discover_recommend_playlist"
        const val CACHE_KEY_RANKING_LIST = "discover_ranking_list"
    }
}