package me.wcy.music.common.dialog.songmenu

import android.view.View

/**
 * Created by wangchenyan.top on 2023/10/11.
 */
interface MenuItem {
    val name: String
    fun onClick(view: View)
}

data class SimpleMenuItem(
    override val name: String,
    val onClick: (View) -> Unit = {}
) : MenuItem {
    override fun onClick(view: View) {
        onClick.invoke(view)
    }
}
