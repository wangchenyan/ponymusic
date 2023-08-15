package me.wcy.music.receiver

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import me.wcy.music.R
import me.wcy.music.application.AppCache
import me.wcy.music.executor.DownloadMusicInfo
import me.wcy.music.utils.ToastUtils
import me.wcy.music.utils.id3.ID3TagUtils
import me.wcy.music.utils.id3.ID3Tags
import java.io.File

/**
 * 下载完成广播接收器
 * Created by hzwangchenyan on 2015/12/30.
 */
class DownloadReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0)
        val downloadMusicInfo: DownloadMusicInfo =
            AppCache.get().getDownloadList().get(id)
        if (downloadMusicInfo != null) {
            ToastUtils.show(context.getString(R.string.download_success, downloadMusicInfo.title))
            val musicPath = downloadMusicInfo.musicPath
            val coverPath = downloadMusicInfo.coverPath
            if (!TextUtils.isEmpty(musicPath) && !TextUtils.isEmpty(coverPath)) {
                // 设置专辑封面
                val musicFile = File(musicPath)
                val coverFile = File(coverPath)
                if (musicFile.exists() && coverFile.exists()) {
                    val id3Tags = ID3Tags.Builder().setCoverFile(coverFile).build()
                    ID3TagUtils.setID3Tags(musicFile, id3Tags, false)
                }
            }
        }
    }
}