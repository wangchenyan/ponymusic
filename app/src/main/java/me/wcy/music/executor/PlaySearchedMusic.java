package me.wcy.music.executor;

import android.app.Activity;
import android.text.TextUtils;

import com.zhy.http.okhttp.OkHttpUtils;

import java.io.File;

import me.wcy.music.callback.JsonCallback;
import me.wcy.music.constants.Constants;
import me.wcy.music.model.JDownloadInfo;
import me.wcy.music.model.JLrc;
import me.wcy.music.model.JSearchMusic;
import me.wcy.music.model.Music;
import me.wcy.music.utils.FileUtils;
import okhttp3.Call;

/**
 * 播放搜索的音乐
 * Created by hzwangchenyan on 2016/1/13.
 */
public abstract class PlaySearchedMusic extends PlayMusic {
    private JSearchMusic.JSong mJSong;

    public PlaySearchedMusic(Activity activity, JSearchMusic.JSong jSong) {
        super(activity, 2);
        mJSong = jSong;
    }

    @Override
    protected void getPlayInfo() {
        String lrcFileName = FileUtils.getLrcFileName(mJSong.getArtistname(), mJSong.getSongname());
        File lrcFile = new File(FileUtils.getLrcDir() + lrcFileName);
        if (!lrcFile.exists()) {
            downloadLrc(lrcFile.getPath());
        } else {
            mCounter++;
        }

        music = new Music();
        music.setType(Music.Type.ONLINE);
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
                        if (response == null || response.getBitrate() == null) {
                            onFail(null);
                            return;
                        }

                        music.setUri(response.getBitrate().getFile_link());
                        music.setDuration(response.getBitrate().getFile_duration() * 1000);
                        checkCounter();
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        onFail(e);
                    }
                });
    }

    private void downloadLrc(final String filePath) {
        OkHttpUtils.get().url(Constants.BASE_URL)
                .addParams(Constants.PARAM_METHOD, Constants.METHOD_LRC)
                .addParams(Constants.PARAM_SONG_ID, mJSong.getSongid())
                .build()
                .execute(new JsonCallback<JLrc>(JLrc.class) {
                    @Override
                    public void onResponse(JLrc response) {
                        if (response == null || TextUtils.isEmpty(response.getLrcContent())) {
                            return;
                        }

                        FileUtils.saveLrcFile(filePath, response.getLrcContent());
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                    }

                    @Override
                    public void onAfter() {
                        checkCounter();
                    }
                });
    }
}
