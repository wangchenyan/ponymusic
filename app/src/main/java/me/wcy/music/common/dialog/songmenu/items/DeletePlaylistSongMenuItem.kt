package me.wcy.music.common.dialog.songmenu.items

import android.view.View
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import me.wcy.music.account.service.UserServiceModule.Companion.userService
import me.wcy.music.common.bean.PlaylistData
import me.wcy.music.common.bean.SongData
import me.wcy.music.common.dialog.songmenu.MenuItem
import me.wcy.music.mine.MineApi
import top.wangchenyan.common.ext.findActivity
import top.wangchenyan.common.ext.showConfirmDialog
import top.wangchenyan.common.ext.toast
import top.wangchenyan.common.ui.activity.BaseActivity

/**
 * Created by wangchenyan.top on 2023/10/11.
 */
class DeletePlaylistSongMenuItem(
    private val playlistData: PlaylistData,
    private val songData: SongData,
    private val onDelete: (songData: SongData) -> Unit,
) : MenuItem {
    override val name: String
        get() = "删除"

    override fun onClick(view: View) {
        val activity = view.context.findActivity() as? BaseActivity
        activity ?: return
        if (activity.application.userService().isLogin().not()) return
        activity.showConfirmDialog(message = "确定将所选音乐从列表删除？") {
            activity.lifecycleScope.launch {
                val result = runCatching {
                    MineApi.get().collectSong(playlistData.id, songData.id.toString(), "del")
                }
                if (result.isSuccess) {
                    val body = result.getOrThrow().body
                    if (body.code == 200) {
                        onDelete.invoke(songData)
                    } else {
                        toast(body.message)
                    }
                } else {
                    toast(result.exceptionOrNull()?.message)
                }
            }
        }
    }
}