package me.wcy.music.discover.home.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import me.wcy.common.ext.toUnMutable
import me.wcy.music.account.service.UserService
import me.wcy.music.common.bean.PlaylistData
import me.wcy.music.discover.DiscoverApi
import me.wcy.music.discover.banner.BannerData
import me.wcy.music.storage.preference.ConfigPreferences
import javax.inject.Inject

/**
 * Created by wangchenyan.top on 2023/9/25.
 */
@HiltViewModel
class DiscoverViewModel @Inject constructor() : ViewModel() {
    private val _bannerList = MutableStateFlow<List<BannerData>>(emptyList())
    val bannerList = _bannerList.toUnMutable()

    private val _recommendPlaylist = MutableStateFlow<List<PlaylistData>>(emptyList())
    val recommendPlaylist = _recommendPlaylist.toUnMutable()

    private val _rankingList = MutableLiveData<List<PlaylistData>>(emptyList())
    val rankingList = _rankingList.toUnMutable()

    @Inject
    lateinit var userService: UserService

    init {
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

    private fun loadBanner() {
        viewModelScope.launch {
            kotlin.runCatching {
                DiscoverApi.get().getBannerList()
            }.onSuccess {
                _bannerList.value = it.banners
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
                _rankingList.value = rankingList
                rankingList.forEach {
                    val d = async {
                        val songListRes = kotlin.runCatching {
                            DiscoverApi.get().getPlaylistSongList(it.id, 3)
                        }
                        if (songListRes.getOrNull()?.code == 200) {
                            it.songList = songListRes.getOrThrow().songs
                            _rankingList.value = rankingList
                        }
                    }
                }
            }.onFailure {
            }
        }
    }
}