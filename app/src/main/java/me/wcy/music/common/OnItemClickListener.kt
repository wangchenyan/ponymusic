package me.wcy.music.common

/**
 * Created by wangchenyan.top on 2023/10/24.
 */
interface OnItemClickListener<T> {
    fun onItemClick(item: T, position: Int)
}