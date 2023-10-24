package me.wcy.music.common

/**
 * Created by wangchenyan.top on 2023/10/11.
 */
interface OnItemClickListener2<T> {
    fun onItemClick(item: T, position: Int)
    fun onMoreClick(item: T, position: Int)
}