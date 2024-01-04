package me.wcy.music.common.dialog.songmenu.items

import android.view.View
import me.wcy.music.common.bean.SongData
import me.wcy.music.common.dialog.songmenu.MenuItem
import me.wcy.music.utils.getSimpleArtist
import top.wangchenyan.common.ext.toast

/**
 * Created by wangchenyan.top on 2023/10/11.
 */
class ArtistMenuItem(private val songData: SongData) : MenuItem {
    override val name: String
        get() = "歌手: ${songData.getSimpleArtist()}"

    override fun onClick(view: View) {
        toast("敬请期待")
    }
}