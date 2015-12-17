package me.wcy.ponymusic.service;

/**
 * 播放进度监听器
 * Created by hzwangchenyan on 2015/12/17.
 */
public interface OnPlayerEventListener {
    /**
     * 更新进度
     */
    void onPublish(int progress);

    /**
     * 切换歌曲
     */
    void onChange(int position);

    /**
     * 暂停播放
     */
    void onPlayerPause();

    /**
     * 继续播放
     */
    void onPlayerResume();
}
