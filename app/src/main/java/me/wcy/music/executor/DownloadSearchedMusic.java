package me.wcy.music.executor;

import android.app.Activity;
import android.text.TextUtils;

import java.io.File;

import me.wcy.music.http.HttpCallback;
import me.wcy.music.http.HttpClient;
import me.wcy.music.model.DownloadInfo;
import me.wcy.music.model.Lrc;
import me.wcy.music.model.SearchMusic;
import me.wcy.music.utils.FileUtils;

/**
 * 下载搜索的音乐
 * Created by hzwangchenyan on 2016/1/13.
 */
public abstract class DownloadSearchedMusic extends DownloadMusic {
    private SearchMusic.Song mSong;

    public DownloadSearchedMusic(Activity activity, SearchMusic.Song song) {
        super(activity);
        mSong = song;
    }

    @Override
    protected void download() {
        // 获取歌曲下载链接
        HttpClient.getMusicDownloadInfo(mSong.getSongid(), new HttpCallback<DownloadInfo>() {
            @Override
            public void onSuccess(DownloadInfo response) {
                if (response == null || response.getBitrate() == null) {
                    onFail(null);
                    return;
                }

                downloadMusic(response.getBitrate().getFile_link(), mSong.getArtistname(), mSong.getSongname());
                onExecuteSuccess(null);
            }

            @Override
            public void onFail(Exception e) {
                onExecuteFail(e);
            }
        });

        // 下载歌词
        String lrcFileName = FileUtils.getLrcFileName(mSong.getArtistname(), mSong.getSongname());
        File lrcFile = new File(FileUtils.getLrcDir() + lrcFileName);
        if (!lrcFile.exists()) {
            HttpClient.getLrc(mSong.getSongid(), new HttpCallback<Lrc>() {
                @Override
                public void onSuccess(Lrc response) {
                    if (response == null || TextUtils.isEmpty(response.getLrcContent())) {
                        return;
                    }

                    String filePath = FileUtils.getLrcDir() + FileUtils.getLrcFileName(mSong.getArtistname(), mSong.getSongname());
                    FileUtils.saveLrcFile(filePath, response.getLrcContent());
                }

                @Override
                public void onFail(Exception e) {
                }
            });
        }
    }
}
