package me.wcy.music.executor

import android.app.Activity
import android.app.Dialog
import android.app.DownloadManager
import android.content.Context
import android.content.DialogInterface
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.appcompat.app.AlertDialog
import me.wcy.music.R
import me.wcy.music.application.AppCache
import me.wcy.music.storage.preference.Preferences
import me.wcy.music.utils.FileUtils
import me.wcy.music.utils.NetworkUtils
import me.wcy.music.utils.ToastUtils

/**
 * Created by hzwangchenyan on 2017/1/20.
 */
abstract class DownloadMusic(private val mActivity: Activity) : IExecutor<Void?> {
    override fun execute() {
        checkNetwork()
    }

    private fun checkNetwork() {
        val mobileNetworkDownload = Preferences.enableMobileNetworkDownload()
        if (NetworkUtils.isActiveNetworkMobile(mActivity) && !mobileNetworkDownload) {
            val builder = AlertDialog.Builder(mActivity)
            builder.setTitle(R.string.tips)
            builder.setMessage(R.string.download_tips)
            builder.setPositiveButton(R.string.download_tips_sure) { dialog: DialogInterface?, which: Int -> downloadWrapper() }
            builder.setNegativeButton(R.string.cancel, null)
            val dialog: Dialog = builder.create()
            dialog.setCanceledOnTouchOutside(false)
            dialog.show()
        } else {
            downloadWrapper()
        }
    }

    private fun downloadWrapper() {
        onPrepare()
        download()
    }

    protected abstract fun download()
    protected fun downloadMusic(url: String?, artist: String?, title: String?, coverPath: String?) {
        try {
            val fileName = FileUtils.getMp3FileName(artist, title)
            val uri = Uri.parse(url)
            val request = DownloadManager.Request(uri)
            request.setTitle(FileUtils.getFileName(artist, title))
            request.setDescription("正在下载…")
            request.setDestinationInExternalPublicDir(FileUtils.relativeMusicDir, fileName)
            request.setMimeType(MimeTypeMap.getFileExtensionFromUrl(url))
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI)
            request.setAllowedOverRoaming(false) // 不允许漫游
            val downloadManager = AppCache.get().getContext()!!
                .getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val id = downloadManager.enqueue(request)
            val musicAbsPath = FileUtils.musicDir + fileName
            val downloadMusicInfo = DownloadMusicInfo(title, musicAbsPath, coverPath)
            AppCache.get().getDownloadList().put(id, downloadMusicInfo)
        } catch (th: Throwable) {
            th.printStackTrace()
            ToastUtils.show("下载失败")
        }
    }
}