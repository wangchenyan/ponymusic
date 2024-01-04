package me.wcy.music.common.dialog.songmenu.items

import android.view.View
import kotlinx.coroutines.CoroutineScope
import me.wcy.music.common.bean.SongData
import me.wcy.music.common.dialog.songmenu.MenuItem
import top.wangchenyan.common.ext.toast

/**
 * Created by wangchenyan.top on 2023/10/11.
 */
class CollectMenuItem(
    private val scope: CoroutineScope,
    private val songData: SongData
) : MenuItem {
    override val name: String
        get() = "收藏到歌单"

    override fun onClick(view: View) {
        toast("敬请期待")
    }
}