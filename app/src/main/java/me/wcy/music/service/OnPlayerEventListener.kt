package me.wcy.music.service

import me.wcy.music.model.Music

/**
 * 播放进度监听器
 * Created by hzwangchenyan on 2015/12/17.
 */
interface OnPlayerEventListener {
    /**
     * 切换歌曲
     */
    fun onChange(music: Music?)

    /**
     * 继续播放
     */
    fun onPlayerStart()

    /**
     * 暂停播放
     */
    fun onPlayerPause()

    /**
     * 更新进度
     */
    fun onPublish(progress: Int)

    /**
     * 缓冲百分比
     */
    fun onBufferingUpdate(percent: Int)
}