package me.wcy.music.mine.like

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import me.wcy.common.ext.toUnMutable
import me.wcy.common.model.CommonResult
import me.wcy.music.account.service.UserService
import me.wcy.music.common.bean.SongData
import me.wcy.music.mine.MineApi
import javax.inject.Inject

/**
 * Created by wangchenyan.top on 2023/9/26.
 */
@HiltViewModel
class LikeSongListViewModel @Inject constructor() : ViewModel() {
    private val _songList = MutableStateFlow<List<SongData>>(emptyList())
    val songList = _songList.toUnMutable()

    @Inject
    lateinit var userService: UserService

    suspend fun loadData(): CommonResult<List<SongData>> {
        val uid = userService.profile.value?.userId ?: return CommonResult.fail(msg = "not login")
        val likeRes = kotlin.runCatching {
            MineApi.get().getLikeSongList(uid)
        }
        if (likeRes.getOrNull()?.code == 200) {
            val likeIds = likeRes.getOrThrow().ids
            if (likeIds.isEmpty()) {
                return CommonResult.success(emptyList())
            }
            val songRes = kotlin.runCatching {
                MineApi.get().getSongDetailById(likeIds.joinToString(","))
            }
            return if (songRes.getOrNull()?.code == 200) {
                val songs = songRes.getOrThrow().songs
                _songList.value = songs
                CommonResult.success(songs)
            } else {
                CommonResult.fail(msg = songRes.exceptionOrNull()?.message)
            }
        } else {
            return CommonResult.fail(msg = likeRes.exceptionOrNull()?.message)
        }
    }
}