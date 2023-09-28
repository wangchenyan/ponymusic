package me.wcy.music.main

import androidx.annotation.DrawableRes
import androidx.fragment.app.Fragment
import me.wcy.music.R
import me.wcy.music.discover.home.DiscoverFragment
import me.wcy.music.mine.home.MineFragment

sealed class NaviTab private constructor(
    val id: String,
    @DrawableRes
    val icon: Int,
    val name: String,
    val newFragment: () -> Fragment
) {
    object Discover : NaviTab(
        "discover",
        R.drawable.ic_tab_discover,
        "发现",
        { DiscoverFragment() }
    )

    object Mine : NaviTab(
        "mine",
        R.drawable.ic_tab_mine,
        "我的",
        { MineFragment() }
    )

    fun getPosition(): Int {
        return ALL.indexOf(this)
    }

    companion object {
        val ALL: List<NaviTab> = listOf(
            Discover, Mine
        )

        fun findByPosition(position: Int): NaviTab? {
            return ALL.getOrNull(position)
        }

        fun findByName(name: String): NaviTab? {
            return ALL.find { it.id == name }
        }
    }
}