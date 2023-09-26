package me.wcy.music.discover.playlist.detail.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import me.wcy.common.ext.toUnMutable
import me.wcy.common.model.CommonResult
import me.wcy.music.common.bean.PlaylistData
import me.wcy.music.common.bean.SongData
import me.wcy.music.discover.DiscoverApi

/**
 * Created by wangchenyan.top on 2023/9/22.
 */
class PlaylistViewModel : ViewModel() {
    private val _playlistData = MutableStateFlow<PlaylistData?>(null)
    val playlistData = _playlistData.toUnMutable()

    private val _songList = MutableStateFlow<List<SongData>>(emptyList())
    val songList = _songList.toUnMutable()

    private var playlistId = 0L

    fun init(playlistId: Long) {
        this.playlistId = playlistId
    }

    suspend fun loadData(): CommonResult<Unit> {
        val detailRes = kotlin.runCatching {
            DiscoverApi.get().getPlaylistDetail(playlistId)
        }
        val songListRes = kotlin.runCatching {
            DiscoverApi.get().getPlaylistSongList(playlistId)
        }
        return if (detailRes.isSuccess.not() || detailRes.getOrThrow().code != 200) {
            CommonResult.fail(msg = detailRes.exceptionOrNull()?.message)
        } else if (songListRes.isSuccess.not() || songListRes.getOrThrow().code != 200) {
            CommonResult.fail(msg = songListRes.exceptionOrNull()?.message)
        } else {
            _playlistData.value = detailRes.getOrThrow().playlist
            _songList.value = songListRes.getOrThrow().songs
            CommonResult.success(Unit)
        }
    }
}