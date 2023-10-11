package me.wcy.music.common.dialog.songmenu.items

import android.view.View
import me.wcy.common.ext.toast
import me.wcy.music.common.bean.PlaylistData
import me.wcy.music.common.bean.SongData
import me.wcy.music.common.dialog.songmenu.MenuItem

/**
 * Created by wangchenyan.top on 2023/10/11.
 */
class DeletePlaylistSongMenuItem(
    private val playlistData: PlaylistData,
    private val songData: SongData
) : MenuItem {
    override val name: String
        get() = "删除"

    override fun onClick(view: View) {
        toast("暂不支持")
    }
}