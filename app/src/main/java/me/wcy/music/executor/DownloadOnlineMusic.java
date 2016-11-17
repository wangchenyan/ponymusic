package me.wcy.music.executor;

import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import java.io.File;

import me.wcy.music.R;
import me.wcy.music.application.MusicApplication;
import me.wcy.music.callback.JsonCallback;
import me.wcy.music.model.JDownloadInfo;
import me.wcy.music.model.JOnlineMusic;
import me.wcy.music.constants.Constants;
import me.wcy.music.utils.FileUtils;
import me.wcy.music.utils.NetworkUtils;
import me.wcy.music.utils.Preferences;
import okhttp3.Call;

/**
 * 下载音乐
 * Created by wcy on 2016/1/3.
 */
public abstract class DownloadOnlineMusic {
    private Context mContext;
    private JOnlineMusic mJOnlineMusic;

    public DownloadOnlineMusic(Context context, JOnlineMusic jOnlineMusic) {
        mContext = context;
        mJOnlineMusic = jOnlineMusic;
    }

    public void execute() {
        checkNetwork();
    }

    private void checkNetwork() {
        boolean mobileNetworkDownload = Preferences.enableMobileNetworkDownload();
        if (NetworkUtils.isActiveNetworkMobile(mContext) && !mobileNetworkDownload) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle(R.string.tips);
            builder.setMessage(R.string.download_tips);
            builder.setPositiveButton(R.string.download_tips_sure, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    download();
                }
            });
            builder.setNegativeButton(R.string.cancel, null);
            Dialog dialog = builder.create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        } else {
            download();
        }
    }

    private void download() {
        onPrepare();
        final DownloadManager downloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
        // 获取歌曲下载链接
        OkHttpUtils.get().url(Constants.BASE_URL)
                .addParams(Constants.PARAM_METHOD, Constants.METHOD_DOWNLOAD_MUSIC)
                .addParams(Constants.PARAM_SONG_ID, mJOnlineMusic.getSong_id())
                .build()
                .execute(new JsonCallback<JDownloadInfo>(JDownloadInfo.class) {
                    @Override
                    public void onResponse(final JDownloadInfo response) {
                        if (response == null) {
                            onFail(null, null);
                            return;
                        }
                        Uri uri = Uri.parse(response.getBitrate().getFile_link());
                        DownloadManager.Request request = new DownloadManager.Request(uri);
                        String mp3FileName = FileUtils.getMp3FileName(mJOnlineMusic.getArtist_name(), mJOnlineMusic.getTitle());
                        request.setDestinationInExternalPublicDir(FileUtils.getRelativeMusicDir(), mp3FileName);
                        request.setMimeType(MimeTypeMap.getFileExtensionFromUrl(response.getBitrate().getFile_link()));
                        request.allowScanningByMediaScanner();
                        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
                        request.setAllowedOverRoaming(false);// 不允许漫游
                        long id = downloadManager.enqueue(request);
                        MusicApplication.getInstance().getDownloadList().put(id, mJOnlineMusic.getTitle());
                        onSuccess();
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        onFail(call, e);
                    }
                });
        // 下载歌词
        String lrcFileName = FileUtils.getLrcFileName(mJOnlineMusic.getArtist_name(), mJOnlineMusic.getTitle());
        File lrcFile = new File(FileUtils.getLrcDir() + lrcFileName);
        if (!TextUtils.isEmpty(mJOnlineMusic.getLrclink()) && !lrcFile.exists()) {
            OkHttpUtils.get().url(mJOnlineMusic.getLrclink()).build()
                    .execute(new FileCallBack(FileUtils.getLrcDir(), lrcFileName) {
                        @Override
                        public void inProgress(float progress, long total) {
                        }

                        @Override
                        public void onResponse(File response) {
                        }

                        @Override
                        public void onError(Call call, Exception e) {
                        }
                    });
        }
    }

    public abstract void onPrepare();

    public abstract void onSuccess();

    public abstract void onFail(Call call, Exception e);
}
