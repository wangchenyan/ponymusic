package me.wcy.music.common.dialog.songmenu.items

import android.view.View
import me.wcy.music.common.bean.SongData
import me.wcy.music.common.dialog.songmenu.MenuItem
import top.wangchenyan.common.ext.toast

/**
 * Created by wangchenyan.top on 2023/10/11.
 */
class AlbumMenuItem(private val songData: SongData) : MenuItem {
    override val name: String
        get() = "专辑: ${songData.al.name}"

    override fun onClick(view: View) {
        toast("敬请期待")
    }
}