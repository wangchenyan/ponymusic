package me.wcy.music.service.likesong

import android.app.Activity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import me.wcy.music.account.service.UserService
import me.wcy.music.mine.MineApi
import top.wangchenyan.common.model.CommonResult
import top.wangchenyan.common.net.apiCall
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by wangchenyan.top on 2024/3/21.
 */
@Singleton
class LikeSongProcessorImpl @Inject constructor(
    private val userService: UserService
) : LikeSongProcessor, CoroutineScope by MainScope() {
    private val likeSongSet = mutableSetOf<Long>()

    override fun init() {
        launch {
            userService.profile.collectLatest {
                if (it != null) {
                    updateLikeSongList()
                } else {
                    likeSongSet.clear()
                }
            }
        }
    }

    override fun updateLikeSongList() {
        if (userService.isLogin().not()) return
        launch {
            val res = runCatching {
                MineApi.get().getMyLikeSongList(userService.getUserId())
            }
            val data = res.getOrNull()
            if (data?.code == 200) {
                likeSongSet.clear()
                likeSongSet.addAll(data.ids)
            }
        }
    }

    override fun isLiked(id: Long): Boolean {
        if (userService.isLogin().not()) {
            return false
        }
        return likeSongSet.contains(id)
    }

    override suspend fun like(activity: Activity, id: Long): CommonResult<Unit> {
        if (userService.isLogin().not()) {
            userService.checkLogin(activity)
            return CommonResult.fail()
        }
        val isLike = isLiked(id)
        if (isLike) {
            val res = apiCall {
                MineApi.get().likeSong(id, false)
            }
            return if (res.isSuccess()) {
                likeSongSet.remove(id)
                updateLikeSongList()
                CommonResult.success(Unit)
            } else {
                CommonResult.fail(res.code, res.msg)
            }
        } else {
            val res = apiCall {
                MineApi.get().likeSong(id, true)
            }
            return if (res.isSuccess()) {
                likeSongSet.add(id)
                updateLikeSongList()
                CommonResult.success(Unit)
            } else {
                CommonResult.fail(res.code, res.msg)
            }
        }
    }
}