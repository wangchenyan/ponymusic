package me.wcy.music.utils;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import com.squareup.okhttp.Request;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import java.io.File;

import me.wcy.music.callback.JsonCallback;
import me.wcy.music.model.JDownloadInfo;
import me.wcy.music.model.JOnlineMusic;

/**
 * 下载音乐
 * Created by wcy on 2016/1/3.
 */
public abstract class DownloadMusic {
    private Context mContext;
    private JOnlineMusic mJOnlineMusic;

    public DownloadMusic(Context context, JOnlineMusic jOnlineMusic) {
        mContext = context;
        mJOnlineMusic = jOnlineMusic;
    }

    public void execute() {
        download();
    }

    private void download() {
        onPrepare();
        final DownloadManager downloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
        // 获取歌曲播放链接
        OkHttpUtils.get().url(Constants.BASE_URL)
                .addParams("method", Constants.METHOD_DOWNLOAD_MUSIC)
                .addParams("songid", mJOnlineMusic.getSong_id())
                .build()
                .execute(new JsonCallback<JDownloadInfo>(JDownloadInfo.class) {
                    @Override
                    public void onResponse(final JDownloadInfo response) {
                        Uri uri = Uri.parse(response.getBitrate().getFile_link());
                        DownloadManager.Request request = new DownloadManager.Request(uri);
                        String mp3FileName = FileUtils.getMp3FileName(mJOnlineMusic.getArtist_name(), mJOnlineMusic.getTitle());
                        request.setDestinationInExternalPublicDir(FileUtils.getRelativeMusicDir(), mp3FileName);
                        request.setMimeType(MimeTypeMap.getFileExtensionFromUrl(response.getBitrate().getFile_link()));
                        request.allowScanningByMediaScanner();
                        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
                        request.setAllowedOverRoaming(false);// 不允许漫游
                        long id = downloadManager.enqueue(request);
                        Preferences.put(mContext, String.valueOf(id), mJOnlineMusic.getTitle());
                        onSuccess();
                    }

                    @Override
                    public void onError(Request request, Exception e) {
                        onFail(request, e);
                    }
                });
        // 下载歌词
        String lrcFileName = FileUtils.getLrcFileName(mJOnlineMusic.getArtist_name(), mJOnlineMusic.getTitle());
        File lrcFile = new File(FileUtils.getLrcDir() + lrcFileName);
        if (!TextUtils.isEmpty(mJOnlineMusic.getLrclink()) && !lrcFile.exists()) {
            OkHttpUtils.get().url(mJOnlineMusic.getLrclink()).build()
                    .execute(new FileCallBack(FileUtils.getLrcDir(), lrcFileName) {
                        @Override
                        public void inProgress(float progress) {
                        }

                        @Override
                        public void onResponse(File response) {
                        }

                        @Override
                        public void onError(Request request, Exception e) {
                        }
                    });
        }
    }

    public abstract void onPrepare();

    public abstract void onSuccess();

    public abstract void onFail(Request request, Exception e);
}
