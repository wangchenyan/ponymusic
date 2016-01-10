package me.wcy.music.utils;

import android.graphics.Bitmap;
import android.text.TextUtils;

import com.squareup.okhttp.Request;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.BitmapCallback;
import com.zhy.http.okhttp.callback.FileCallBack;

import java.io.File;

import me.wcy.music.callback.JsonCallback;
import me.wcy.music.enums.MusicTypeEnum;
import me.wcy.music.model.JDownloadInfo;
import me.wcy.music.model.JOnlineMusic;
import me.wcy.music.model.Music;

/**
 * 播放在线音乐
 * Created by wcy on 2016/1/3.
 */
public abstract class PlayMusic {
    private JOnlineMusic mJOnlineMusic;
    private int mPlayCounter = 0;

    public PlayMusic(JOnlineMusic jOnlineMusic) {
        mJOnlineMusic = jOnlineMusic;
    }

    public void execute() {
        getPlayInfo();
    }

    private void getPlayInfo() {
        onPrepare();
        String lrcFileName = FileUtils.getLrcFileName(mJOnlineMusic.getArtist_name(), mJOnlineMusic.getTitle());
        File lrcFile = new File(FileUtils.getLrcDir() + lrcFileName);
        if (TextUtils.isEmpty(mJOnlineMusic.getLrclink()) || lrcFile.exists()) {
            mPlayCounter++;
        }
        String picUrl = TextUtils.isEmpty(mJOnlineMusic.getPic_big()) ? TextUtils.isEmpty(mJOnlineMusic.getPic_small())
                ? null : mJOnlineMusic.getPic_small() : mJOnlineMusic.getPic_big();
        if (TextUtils.isEmpty(picUrl)) {
            mPlayCounter++;
        }
        final Music music = new Music();
        music.setType(MusicTypeEnum.ONLINE);
        music.setTitle(mJOnlineMusic.getTitle());
        music.setArtist(mJOnlineMusic.getArtist_name());
        music.setAlbum(mJOnlineMusic.getAlbum_title());
        // 获取歌曲播放链接
        OkHttpUtils.get().url(Constants.BASE_URL)
                .addParams("method", Constants.METHOD_DOWNLOAD_MUSIC)
                .addParams("songid", mJOnlineMusic.getSong_id())
                .build()
                .execute(new JsonCallback<JDownloadInfo>(JDownloadInfo.class) {
                    @Override
                    public void onResponse(final JDownloadInfo response) {
                        music.setUri(response.getBitrate().getFile_link());
                        music.setDuration(response.getBitrate().getFile_duration() * 1000);
                        mPlayCounter++;
                        if (mPlayCounter == 3) {
                            onSuccess(music);
                        }
                    }

                    @Override
                    public void onError(Request request, Exception e) {
                        onFail(request, e);
                    }
                });
        // 下载歌词
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

                        @Override
                        public void onAfter() {
                            mPlayCounter++;
                            if (mPlayCounter == 3) {
                                onSuccess(music);
                            }
                        }
                    });
        }
        // 下载歌曲封面
        if (!TextUtils.isEmpty(picUrl)) {
            OkHttpUtils.get().url(picUrl).build()
                    .execute(new BitmapCallback() {
                        @Override
                        public void onResponse(Bitmap bitmap) {
                            music.setCover(bitmap);
                            mPlayCounter++;
                            if (mPlayCounter == 3) {
                                onSuccess(music);
                            }
                        }

                        @Override
                        public void onError(Request request, Exception e) {
                            mPlayCounter++;
                            if (mPlayCounter == 3) {
                                onSuccess(music);
                            }
                        }
                    });
        }
    }

    public abstract void onPrepare();

    public abstract void onSuccess(Music music);

    public abstract void onFail(Request request, Exception e);
}
