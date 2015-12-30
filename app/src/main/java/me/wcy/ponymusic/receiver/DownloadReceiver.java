package me.wcy.ponymusic.receiver;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import me.wcy.ponymusic.R;
import me.wcy.ponymusic.utils.Preferences;
import me.wcy.ponymusic.utils.ToastUtil;

/**
 * 下载完成广播接收器
 * Created by hzwangchenyan on 2015/12/30.
 */
public class DownloadReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
        String title = (String) Preferences.get(context, String.valueOf(id), "");
        if (TextUtils.isEmpty(title)) {
            return;
        }
        ToastUtil.show(title + context.getString(R.string.download_success));
    }
}
