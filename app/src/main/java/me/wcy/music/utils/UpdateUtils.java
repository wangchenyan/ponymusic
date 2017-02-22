package me.wcy.music.utils;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.webkit.MimeTypeMap;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import im.fir.sdk.FIR;
import im.fir.sdk.VersionCheckCallback;
import me.wcy.music.BuildConfig;
import me.wcy.music.R;
import me.wcy.music.activity.AboutActivity;
import me.wcy.music.api.KeyStore;
import me.wcy.music.application.AppCache;
import me.wcy.music.constants.Extras;
import me.wcy.music.model.UpdateInfo;

public class UpdateUtils {

    public static void checkUpdate(final Activity activity) {
        FIR.checkForUpdateInFIR(KeyStore.getKey(KeyStore.FIR_KEY), new VersionCheckCallback() {
            @Override
            public void onStart() {
                if (activity instanceof AboutActivity) {
                    ToastUtils.show("正在检查更新");
                }
            }

            @Override
            public void onSuccess(String versionJson) {
                if (activity.isFinishing()) {
                    return;
                }
                Gson gson = new Gson();
                UpdateInfo updateInfo;
                try {
                    updateInfo = gson.fromJson(versionJson, UpdateInfo.class);
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                    return;
                }
                int version = Integer.valueOf(updateInfo.version);
                if (version > BuildConfig.VERSION_CODE) {
                    updateDialog(activity, updateInfo);
                } else {
                    if (activity instanceof AboutActivity) {
                        ToastUtils.show("已是最新版本");
                    }
                }
            }

            @Override
            public void onFail(Exception exception) {
            }

            @Override
            public void onFinish() {
            }
        });
    }

    private static void updateDialog(final Activity activity, final UpdateInfo updateInfo) {
        String message = String.format("v %1$s(%2$sMB)\n\n%3$s", updateInfo.versionShort,
                FileUtils.b2mb(updateInfo.binary.fsize), updateInfo.changelog);
        new AlertDialog.Builder(activity)
                .setTitle("发现新版本")
                .setMessage(message)
                .setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        download(activity, updateInfo);
                    }
                })
                .setNegativeButton("稍后提醒", null)
                .show();
    }

    private static void download(Activity activity, UpdateInfo updateInfo) {
        String fileName = String.format("PonyMusic_%s.apk", updateInfo.versionShort);
        DownloadManager downloadManager = (DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(updateInfo.installUrl);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setTitle(activity.getString(R.string.app_name));
        request.setDescription("正在更新…");
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
        request.setMimeType(MimeTypeMap.getFileExtensionFromUrl(updateInfo.installUrl));
        request.allowScanningByMediaScanner();
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        request.setAllowedOverRoaming(false);// 不允许漫游
        long id = downloadManager.enqueue(request);
        AppCache.getDownloadList().put(id, Extras.DOWNLOAD_UPDATE);
        ToastUtils.show("正在后台下载");
    }
}
