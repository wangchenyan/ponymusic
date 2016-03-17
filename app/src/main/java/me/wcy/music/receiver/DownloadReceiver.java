package me.wcy.music.receiver;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import me.wcy.music.R;
import me.wcy.music.application.MusicApplication;
import me.wcy.music.utils.ToastUtils;

/**
 * 下载完成广播接收器
 * Created by hzwangchenyan on 2015/12/30.
 */
public class DownloadReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
        String title = MusicApplication.getInstance().getDownloadList().get(id);
        if (TextUtils.isEmpty(title)) {
            return;
        }
        ToastUtils.show(context.getString(R.string.download_success, title));
    }
}
