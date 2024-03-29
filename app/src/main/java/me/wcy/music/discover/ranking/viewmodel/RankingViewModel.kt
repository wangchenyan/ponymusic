package me.wcy.music.discover.ranking.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import me.wcy.music.discover.DiscoverApi
import top.wangchenyan.common.ext.toUnMutable
import top.wangchenyan.common.model.CommonResult

/**
 * Created by wangchenyan.top on 2023/10/25.
 */
class RankingViewModel : ViewModel() {
    private val _rankingList = MutableLiveData<List<Any>>()
    val rankingList = _rankingList.toUnMutable()

    suspend fun loadData(): CommonResult<Unit> {
        val rankingListRes = kotlin.runCatching {
            DiscoverApi.get().getRankingList()
        }
        if (rankingListRes.getOrNull()?.code == 200) {
            val rankingList = rankingListRes.getOrThrow().playlists
            val officialList = rankingList.filter { it.toplistType.isNotEmpty() }
            val selectedList = rankingList.filter { it.toplistType.isEmpty() }
            val finalList =
                listOf(TitleData("官方榜")) + officialList + listOf(TitleData("精选榜")) + selectedList
            _rankingList.value = finalList
            viewModelScope.launch {
                officialList.forEach {
                    val d = async {
                        val songListRes = kotlin.runCatching {
                            DiscoverApi.get().getPlaylistSongList(it.id, limit = 3)
                        }
                        if (songListRes.getOrNull()?.code == 200) {
                            it.songList = songListRes.getOrThrow().songs
                            _rankingList.value = finalList
                        }
                    }
                }
            }
            return CommonResult.success(Unit)
        } else {
            return CommonResult.fail(msg = rankingListRes.exceptionOrNull()?.message)
        }
    }

    data class TitleData(val title: CharSequence)
}