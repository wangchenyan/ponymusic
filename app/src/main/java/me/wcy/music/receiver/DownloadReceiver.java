package me.wcy.music.receiver;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import me.wcy.music.R;
import me.wcy.music.application.AppCache;
import me.wcy.music.utils.ToastUtils;

/**
 * 下载完成广播接收器
 * Created by hzwangchenyan on 2015/12/30.
 */
public class DownloadReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        if (action.equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
            String title = AppCache.getDownloadList().get(id);
            if (!TextUtils.isEmpty(title)) {
                ToastUtils.show(context.getString(R.string.download_success, title));
            }
        } else if (action.equals(DownloadManager.ACTION_NOTIFICATION_CLICKED)) {
            // 点击通知取消下载
            long[] ids = intent.getLongArrayExtra(DownloadManager.EXTRA_NOTIFICATION_CLICK_DOWNLOAD_IDS);
            manager.remove(ids);
            ToastUtils.show("已取消下载");
        }
    }
}
