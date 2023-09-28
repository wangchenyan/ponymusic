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
    private val _likePlaylist = MutableStateFlow<PlaylistData?>(null)
    val likePlaylist = _likePlaylist.toUnMutable()
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
                    updatePlaylist(profile.userId)
                }
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
                val mineList = list.filter { it.userId == uid }
                _likePlaylist.value = mineList.firstOrNull()
                _myPlaylists.value = mineList.takeLast((mineList.size - 1).coerceAtLeast(0))
                _collectPlaylists.value = list.filter { it.userId != uid }
            }
        }
    }
}