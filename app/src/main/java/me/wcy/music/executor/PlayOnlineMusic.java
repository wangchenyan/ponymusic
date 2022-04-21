package me.wcy.music.executor;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;

import me.wcy.music.http.HttpCallback;
import me.wcy.music.http.HttpClient;
import me.wcy.music.model.DownloadInfo;
import me.wcy.music.model.Music;
import me.wcy.music.model.OnlineMusic;
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

    // 获取音乐播放信息
    @Override
    protected void getPlayInfo() {
        String artist = mOnlineMusic.getArtist_name();
        String title = mOnlineMusic.getTitle();

        // OnlineMusic -> Music
        // music 为 PlayMusic 中声明的 Music 对象
        music = new Music();
        music.setType(Music.Type.ONLINE);
        music.setTitle(title);
        music.setArtist(artist);
        music.setAlbum(mOnlineMusic.getAlbum_title());

        // 下载歌词
//        String lrcFileName = FileUtils.getLrcFileName(artist, title);
//        File lrcFile = new File(FileUtils.getLrcDir() + lrcFileName);
//        if (!lrcFile.exists() && !TextUtils.isEmpty(mOnlineMusic.getLrclink())) {
//            downloadLrc(mOnlineMusic.getLrclink(), lrcFileName);
//        } else {
//            mCounter++;
//        }

        // 下载封面
        String picUrl = mOnlineMusic.getPic_big();
        String albumFileName = FileUtils.getFileNameByUrl(artist, title, picUrl);
        File albumFile = new File(FileUtils.getAlbumDir(), albumFileName);
        Log.d("TAG", "getPlayInfo: "+ albumFileName);

        if (TextUtils.isEmpty(picUrl)) {
            picUrl = mOnlineMusic.getPic_small();
        }
        if (!albumFile.exists() && !TextUtils.isEmpty(picUrl)) {
            Log.d("TAG-download", "getPlayInfo: "+picUrl);
            downloadAlbum(picUrl, albumFileName);
        } else {
            mCounter++;
        }
        music.setCoverPath(albumFile.getPath());

//目前就是要
        // 获取歌曲播放链接
        HttpClient.getMusicDownloadInfo(mOnlineMusic.getSong_id(), new HttpCallback<DownloadInfo>() {
//        HttpClient.getMusicDownloadInfo(mOnlineMusic.getAudioUrl(), new HttpCallback<DownloadInfo>() {
        @Override
            public void onSuccess(DownloadInfo response) {
//                if (response == null || response.getBitrate() == null) {
            if (response == null || response.getAudioUrl() == null) {
                onFail(null);
                    return;
                }
            Log.d("TAG-play", "onSuccess: "+response.getAudioUrl());
//                music.setPath(response.getBitrate().getFile_link());
//                music.setDuration(response.getBitrate().getFile_duration() * 1000);
                music.setPath(response.getAudioUrl());
//                music.setDuration(response.getBitrate().getFile_duration() * 1000);
                checkCounter();
            }

            @Override
            public void onFail(Exception e) {
                onExecuteFail(e);
            }
        });
    }

    private void downloadLrc(String url, String fileName) {
        HttpClient.downloadFile(url, FileUtils.getLrcDir(), fileName, new HttpCallback<File>() {
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

    private void downloadAlbum(String picUrl, String fileName) {
        HttpClient.downloadFile(picUrl, FileUtils.getAlbumDir(), fileName, new HttpCallback<File>() {
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
}
