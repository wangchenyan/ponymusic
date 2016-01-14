package me.wcy.music.executor;

import android.text.TextUtils;

import com.squareup.okhttp.Request;
import com.zhy.http.okhttp.OkHttpUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import me.wcy.music.callback.JsonCallback;
import me.wcy.music.enums.MusicTypeEnum;
import me.wcy.music.model.JDownloadInfo;
import me.wcy.music.model.JLrc;
import me.wcy.music.model.JSearchMusic;
import me.wcy.music.model.Music;
import me.wcy.music.utils.Constants;
import me.wcy.music.utils.FileUtils;

/**
 * 播放搜索的音乐
 * Created by hzwangchenyan on 2016/1/13.
 */
public abstract class PlaySearchedMusic {
    private JSearchMusic.JSong mJSong;
    private int mCounter = 0;

    public PlaySearchedMusic(JSearchMusic.JSong jSong) {
        mJSong = jSong;
    }

    public void execute() {
        getPlayInfo();
    }

    private void getPlayInfo() {
        onPrepare();
        String lrcFileName = FileUtils.getLrcFileName(mJSong.getArtistname(), mJSong.getSongname());
        File lrcFile = new File(FileUtils.getLrcDir() + lrcFileName);
        if (lrcFile.exists()) {
            mCounter++;
        }
        final Music music = new Music();
        music.setType(MusicTypeEnum.ONLINE);
        music.setTitle(mJSong.getSongname());
        music.setArtist(mJSong.getArtistname());
        // 获取歌曲播放链接
        OkHttpUtils.get().url(Constants.BASE_URL)
                .addParams(Constants.PARAM_METHOD, Constants.METHOD_DOWNLOAD_MUSIC)
                .addParams(Constants.PARAM_SONG_ID, mJSong.getSongid())
                .build()
                .execute(new JsonCallback<JDownloadInfo>(JDownloadInfo.class) {
                    @Override
                    public void onResponse(final JDownloadInfo response) {
                        music.setUri(response.getBitrate().getFile_link());
                        music.setDuration(response.getBitrate().getFile_duration() * 1000);
                        mCounter++;
                        if (mCounter == 2) {
                            onSuccess(music);
                        }
                    }

                    @Override
                    public void onError(Request request, Exception e) {
                        onFail(request, e);
                    }
                });
        // 下载歌词
        OkHttpUtils.get().url(Constants.BASE_URL)
                .addParams(Constants.PARAM_METHOD, Constants.METHOD_LRC)
                .addParams(Constants.PARAM_SONG_ID, mJSong.getSongid())
                .build()
                .execute(new JsonCallback<JLrc>(JLrc.class) {
                    @Override
                    public void onResponse(JLrc response) {
                        if (TextUtils.isEmpty(response.getLrcContent())) {
                            return;
                        }
                        String lrcFileName = FileUtils.getLrcFileName(mJSong.getArtistname(), mJSong.getSongname());
                        saveLrcFile(lrcFileName, response.getLrcContent());
                    }

                    @Override
                    public void onError(Request request, Exception e) {
                    }

                    @Override
                    public void onAfter() {
                        mCounter++;
                        if (mCounter == 2) {
                            onSuccess(music);
                        }
                    }
                });
    }

    private void saveLrcFile(String fileName, String content) {
        try {
            FileWriter writer = new FileWriter(FileUtils.getLrcDir() + fileName);
            writer.flush();
            writer.write(content);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public abstract void onPrepare();

    public abstract void onSuccess(Music music);

    public abstract void onFail(Request request, Exception e);
}
