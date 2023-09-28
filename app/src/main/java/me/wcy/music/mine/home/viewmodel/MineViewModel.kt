package me.wcy.music.mine.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import me.wcy.common.ext.toUnMutable
import me.wcy.music.account.service.UserService
import me.wcy.music.common.bean.PlaylistData
import me.wcy.music.mine.MineApi
import javax.inject.Inject

/**
 * Created by wangchenyan.top on 2023/9/28.
 */
@HiltViewModel
class MineViewModel @Inject constructor() : ViewModel() {
    private val _likeSongData = MutableStateFlow(LikeSongData())
    val likeSongData = _likeSongData.toUnMutable()

    private val _myPlaylists = MutableStateFlow<List<PlaylistData>>(emptyList())
    val myPlaylists = _myPlaylists
    private val _collectPlaylists = MutableStateFlow<List<PlaylistData>>(emptyList())
    val collectPlaylists = _collectPlaylists

    @Inject
    lateinit var userService: UserService

    init {
        viewModelScope.launch {
            userService.profile.collectLatest { profile ->
                if (profile != null) {
                    updateLikeSong(profile.userId)
                    updatePlaylist(profile.userId)
                }
            }
        }
    }

    private fun updateLikeSong(uid: Long) {
        viewModelScope.launch {
            val likeRes = kotlin.runCatching {
                MineApi.get().getLikeSongList(uid)
            }
            if (likeRes.getOrNull()?.code == 200) {
                val likeIds = likeRes.getOrThrow().ids
                val likeSongData = LikeSongData(count = likeIds.size)
                if (likeIds.isEmpty()) {
                    _likeSongData.value = likeSongData
                    return@launch
                }
                val songRes = kotlin.runCatching {
                    MineApi.get().getSongDetailById(likeIds.first().toString())
                }
                if (songRes.getOrNull()?.code == 200) {
                    likeSongData.cover = songRes.getOrThrow().songs.firstOrNull()?.al?.picUrl ?: ""
                }
                _likeSongData.value = likeSongData
            }
        }
    }

    private fun updatePlaylist(uid: Long) {
        viewModelScope.launch {
            val res = kotlin.runCatching {
                MineApi.get().getUserPlaylist(uid)
            }
            if (res.getOrNull()?.code == 200) {
                val list = res.getOrThrow().playlists
                _myPlaylists.value = list.filter { it.creator.userId == uid }
                _collectPlaylists.value = list.filter { it.creator.userId != uid }
            }
        }
    }

    data class LikeSongData(
        var cover: String = "",
        var count: Int = 0
    )
}