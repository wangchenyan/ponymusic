package me.wcy.music.executor;

import android.app.Activity;
import android.text.TextUtils;

import com.zhy.http.okhttp.OkHttpUtils;

import java.io.File;

import me.wcy.music.application.AppCache;
import me.wcy.music.callback.JsonCallback;
import me.wcy.music.constants.Constants;
import me.wcy.music.model.JDownloadInfo;
import me.wcy.music.model.JLrc;
import me.wcy.music.model.JSearchMusic;
import me.wcy.music.utils.FileUtils;
import okhttp3.Call;

/**
 * 下载搜索的音乐
 * Created by hzwangchenyan on 2016/1/13.
 */
public abstract class DownloadSearchedMusic extends DownloadMusic {
    private JSearchMusic.JSong mJSong;

    public DownloadSearchedMusic(Activity activity, JSearchMusic.JSong jSong) {
        super(activity);
        mJSong = jSong;
    }

    @Override
    protected void download() {
        // 获取歌曲下载链接
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

                        long id = downloadMusic(response.getBitrate().getFile_link(), mJSong.getArtistname(), mJSong.getSongname());
                        AppCache.getDownloadList().put(id, mJSong.getSongname());
                        onSuccess(null);
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        onFail(e);
                    }
                });

        // 下载歌词
        String lrcFileName = FileUtils.getLrcFileName(mJSong.getArtistname(), mJSong.getSongname());
        File lrcFile = new File(FileUtils.getLrcDir() + lrcFileName);
        if (!lrcFile.exists()) {
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

                            String filePath = FileUtils.getLrcDir() + FileUtils.getLrcFileName(mJSong.getArtistname(), mJSong.getSongname());
                            FileUtils.saveLrcFile(filePath, response.getLrcContent());
                        }

                        @Override
                        public void onError(Call call, Exception e) {
                        }
                    });
        }
    }
}
