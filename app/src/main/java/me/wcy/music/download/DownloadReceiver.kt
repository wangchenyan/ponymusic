package me.wcy.music.download

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.LongSparseArray
import top.wangchenyan.common.ext.toast
import me.wcy.music.R

/**
 * 下载完成广播接收器
 * Created by hzwangchenyan on 2015/12/30.
 */
class DownloadReceiver : BroadcastReceiver() {
    private val mDownloadList = LongSparseArray<DownloadMusicInfo>()

    override fun onReceive(context: Context, intent: Intent) {
        val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0)
        val downloadMusicInfo = mDownloadList.get(id)
        if (downloadMusicInfo != null) {
            toast(context.getString(R.string.download_success, downloadMusicInfo.title))
        }
    }
}