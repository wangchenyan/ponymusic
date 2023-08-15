package me.wcy.music.executor

/**
 * 如果本地歌曲没有歌词则从网络搜索歌词
 * Created by wcy on 2016/4/26.
 */
abstract class SearchLrc(private val artist: String?, private val title: String?) :
    IExecutor<String?> {
    override fun execute() {
        onPrepare()
        searchLrc()
    }

    private fun searchLrc() {
    }

    private fun downloadLrc(songId: String?) {
    }
}