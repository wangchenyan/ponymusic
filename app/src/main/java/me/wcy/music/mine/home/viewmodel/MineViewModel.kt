package me.wcy.music.mine.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import me.wcy.music.account.service.UserService
import me.wcy.music.common.bean.PlaylistData
import me.wcy.music.mine.MineApi
import me.wcy.music.net.NetCache
import top.wangchenyan.common.ext.toUnMutable
import top.wangchenyan.common.model.CommonResult
import top.wangchenyan.common.net.apiCall
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

    private var updateJob: Job? = null

    init {
        viewModelScope.launch {
            userService.profile.collectLatest { profile ->
                if (profile != null) {
                    updatePlaylist(profile.userId)
                } else {
                    _likePlaylist.value = null
                    _myPlaylists.value = emptyList()
                    _collectPlaylists.value = emptyList()
                }
            }
        }
    }

    fun updatePlaylistFromCache() {
        viewModelScope.launch {
            if (userService.isLogin()) {
                val uid = userService.profile.value?.userId ?: return@launch
                val cacheList = NetCache.userCache.getJsonArray(CACHE_KEY, PlaylistData::class.java)
                    ?: return@launch
                notifyPlaylist(uid, cacheList)
            }
        }
    }

    fun updatePlaylist() {
        if (userService.isLogin()) {
            val uid = userService.profile.value?.userId ?: return
            updatePlaylist(uid)
        }
    }

    private fun updatePlaylist(uid: Long) {
        updateJob?.cancel()
        updateJob = viewModelScope.launch {
            val res = kotlin.runCatching {
                MineApi.get().getUserPlaylist(uid)
            }
            if (res.getOrNull()?.code == 200) {
                val list = res.getOrThrow().playlists
                notifyPlaylist(uid, list)
                NetCache.userCache.putJson(CACHE_KEY, list)
            }
        }
    }

    private fun notifyPlaylist(uid: Long, list: List<PlaylistData>) {
        val mineList = list.filter { it.userId == uid }
        _likePlaylist.value = mineList.firstOrNull()
        _myPlaylists.value = mineList.takeLast((mineList.size - 1).coerceAtLeast(0))
        _collectPlaylists.value = list.filter { it.userId != uid }
    }

    suspend fun removeCollect(id: Long): CommonResult<Unit> {
        val res = apiCall { MineApi.get().collectPlaylist(id, 2) }
        return if (res.isSuccess()) {
            val list = _collectPlaylists.value
            _collectPlaylists.value = list.toMutableList().apply {
                removeAll { it.id == id }
            }
            CommonResult.success(Unit)
        } else {
            CommonResult.fail(res.code, res.msg)
        }
    }

    companion object {
        private const val CACHE_KEY = "my_playlist"
    }
}