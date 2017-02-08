package me.wcy.music.executor;

import android.app.Activity;
import android.text.TextUtils;

import java.io.File;

import me.wcy.music.http.HttpCallback;
import me.wcy.music.http.HttpClient;
import me.wcy.music.model.DownloadInfo;
import me.wcy.music.model.OnlineMusic;
import me.wcy.music.utils.FileUtils;

/**
 * 下载音乐
 * Created by wcy on 2016/1/3.
 */
public abstract class DownloadOnlineMusic extends DownloadMusic {
    private OnlineMusic mOnlineMusic;

    public DownloadOnlineMusic(Activity activity, OnlineMusic onlineMusic) {
        super(activity);
        mOnlineMusic = onlineMusic;
    }

    @Override
    protected void download() {
        // 获取歌曲下载链接
        HttpClient.getMusicDownloadInfo(mOnlineMusic.getSong_id(), new HttpCallback<DownloadInfo>() {
            @Override
            public void onSuccess(DownloadInfo response) {
                if (response == null || response.getBitrate() == null) {
                    onFail(null);
                    return;
                }

                downloadMusic(response.getBitrate().getFile_link(), mOnlineMusic.getArtist_name(), mOnlineMusic.getTitle());
                onExecuteSuccess(null);
            }

            @Override
            public void onFail(Exception e) {
                onExecuteFail(e);
            }
        });

        // 下载歌词
        String lrcFileName = FileUtils.getLrcFileName(mOnlineMusic.getArtist_name(), mOnlineMusic.getTitle());
        File lrcFile = new File(FileUtils.getLrcDir() + lrcFileName);
        if (!TextUtils.isEmpty(mOnlineMusic.getLrclink()) && !lrcFile.exists()) {
            HttpClient.downloadFile(mOnlineMusic.getLrclink(), FileUtils.getLrcDir(), lrcFileName, new HttpCallback<File>() {
                @Override
                public void onSuccess(File file) {
                }

                @Override
                public void onFail(Exception e) {
                }
            });
        }
    }
}
