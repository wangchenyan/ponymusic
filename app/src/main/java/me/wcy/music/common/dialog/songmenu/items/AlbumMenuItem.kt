package me.wcy.music.common.dialog.songmenu.items

import android.view.View
import top.wangchenyan.common.ext.toast
import me.wcy.music.common.bean.SongData
import me.wcy.music.common.dialog.songmenu.MenuItem

/**
 * Created by wangchenyan.top on 2023/10/11.
 */
class AlbumMenuItem(private val songData: SongData) : MenuItem {
    override val name: String
        get() = "专辑: ${songData.al.name}"

    override fun onClick(view: View) {
        toast("暂不支持")
    }
}