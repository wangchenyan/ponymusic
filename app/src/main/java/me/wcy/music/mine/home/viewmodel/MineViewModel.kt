package me.wcy.music.mine.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import me.wcy.common.ext.toUnMutable
import me.wcy.music.account.service.UserService
import me.wcy.music.mine.MineApi
import javax.inject.Inject

/**
 * Created by wangchenyan.top on 2023/9/28.
 */
@HiltViewModel
class MineViewModel @Inject constructor() : ViewModel() {
    private val _likeSongData = MutableStateFlow(LikeSongData())
    val likeSongData = _likeSongData.toUnMutable()

    @Inject
    lateinit var userService: UserService

    init {
        viewModelScope.launch {
            userService.profile.collectLatest { profile ->
                if (profile != null) {
                    updateLikeSongData(profile.userId)
                }
            }
        }
    }

    private fun updateLikeSongData(uid: Long) {
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

    data class LikeSongData(
        var cover: String = "",
        var count: Int = 0
    )
}