package me.wcy.music.executor;

import android.app.Activity;
import android.graphics.Bitmap;
import android.text.TextUtils;

import java.io.File;

import me.wcy.music.http.HttpCallback;
import me.wcy.music.http.HttpClient;
import me.wcy.music.model.DownloadInfo;
import me.wcy.music.model.OnlineMusic;
import me.wcy.music.model.Music;
import me.wcy.music.utils.FileUtils;

/**
 * 播放在线音乐
 * Created by wcy on 2016/1/3.
 */
public abstract class PlayOnlineMusic extends PlayMusic {
    private OnlineMusic mOnlineMusic;

    public PlayOnlineMusic(Activity activity, OnlineMusic onlineMusic) {
        super(activity, 3);
        mOnlineMusic = onlineMusic;
    }

    @Override
    protected void getPlayInfo() {
        String lrcFileName = FileUtils.getLrcFileName(mOnlineMusic.getArtist_name(), mOnlineMusic.getTitle());
        File lrcFile = new File(FileUtils.getLrcDir() + lrcFileName);
        if (!TextUtils.isEmpty(mOnlineMusic.getLrclink()) && !lrcFile.exists()) {
            downloadLrc(lrcFileName);
        } else {
            mCounter++;
        }

        String picUrl = mOnlineMusic.getPic_big();
        if (TextUtils.isEmpty(picUrl)) {
            picUrl = mOnlineMusic.getPic_small();
        }
        if (!TextUtils.isEmpty(picUrl)) {
            downloadAlbum(picUrl);
        } else {
            mCounter++;
        }

        music = new Music();
        music.setType(Music.Type.ONLINE);
        music.setTitle(mOnlineMusic.getTitle());
        music.setArtist(mOnlineMusic.getArtist_name());
        music.setAlbum(mOnlineMusic.getAlbum_title());

        // 获取歌曲播放链接
        HttpClient.getMusicDownloadInfo(mOnlineMusic.getSong_id(), new HttpCallback<DownloadInfo>() {
            @Override
            public void onSuccess(DownloadInfo response) {
                if (response == null || response.getBitrate() == null) {
                    onFail(null);
                    return;
                }

                music.setUri(response.getBitrate().getFile_link());
                music.setDuration(response.getBitrate().getFile_duration() * 1000);
                checkCounter();
            }

            @Override
            public void onFail(Exception e) {
                onExecuteFail(e);
            }
        });
    }

    private void downloadLrc(String fileName) {
        HttpClient.downloadFile(mOnlineMusic.getLrclink(), FileUtils.getLrcDir(), fileName, new HttpCallback<File>() {
            @Override
            public void onSuccess(File file) {
            }

            @Override
            public void onFail(Exception e) {
            }

            @Override
            public void onFinish() {
                checkCounter();
            }
        });
    }

    private void downloadAlbum(String picUrl) {
        HttpClient.getBitmap(picUrl, new HttpCallback<Bitmap>() {
            @Override
            public void onSuccess(Bitmap bitmap) {
                music.setCover(bitmap);
            }

            @Override
            public void onFail(Exception e) {
            }

            @Override
            public void onFinish() {
                checkCounter();
            }
        });
    }
}
