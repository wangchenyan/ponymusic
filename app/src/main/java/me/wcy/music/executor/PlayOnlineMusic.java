package me.wcy.music.executor;

import android.app.Activity;
import android.graphics.Bitmap;
import android.text.TextUtils;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.BitmapCallback;
import com.zhy.http.okhttp.callback.FileCallBack;

import java.io.File;

import me.wcy.music.callback.JsonCallback;
import me.wcy.music.constants.Constants;
import me.wcy.music.model.JDownloadInfo;
import me.wcy.music.model.JOnlineMusic;
import me.wcy.music.model.Music;
import me.wcy.music.utils.FileUtils;
import okhttp3.Call;

/**
 * 播放在线音乐
 * Created by wcy on 2016/1/3.
 */
public abstract class PlayOnlineMusic extends PlayMusic {
    private JOnlineMusic mJOnlineMusic;

    public PlayOnlineMusic(Activity activity, JOnlineMusic jOnlineMusic) {
        super(activity, 3);
        mJOnlineMusic = jOnlineMusic;
    }

    @Override
    protected void getPlayInfo() {
        String lrcFileName = FileUtils.getLrcFileName(mJOnlineMusic.getArtist_name(), mJOnlineMusic.getTitle());
        File lrcFile = new File(FileUtils.getLrcDir() + lrcFileName);
        if (!TextUtils.isEmpty(mJOnlineMusic.getLrclink()) && !lrcFile.exists()) {
            downloadLrc(lrcFileName);
        } else {
            mCounter++;
        }

        String picUrl = mJOnlineMusic.getPic_big();
        if (TextUtils.isEmpty(picUrl)) {
            picUrl = mJOnlineMusic.getPic_small();
        }
        if (!TextUtils.isEmpty(picUrl)) {
            downloadAlbum(picUrl);
        } else {
            mCounter++;
        }

        music = new Music();
        music.setType(Music.Type.ONLINE);
        music.setTitle(mJOnlineMusic.getTitle());
        music.setArtist(mJOnlineMusic.getArtist_name());
        music.setAlbum(mJOnlineMusic.getAlbum_title());

        // 获取歌曲播放链接
        OkHttpUtils.get().url(Constants.BASE_URL)
                .addParams(Constants.PARAM_METHOD, Constants.METHOD_DOWNLOAD_MUSIC)
                .addParams(Constants.PARAM_SONG_ID, mJOnlineMusic.getSong_id())
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

    private void downloadLrc(String fileName) {
        OkHttpUtils.get().url(mJOnlineMusic.getLrclink()).build()
                .execute(new FileCallBack(FileUtils.getLrcDir(), fileName) {
                    @Override
                    public void inProgress(float progress, long total) {
                    }

                    @Override
                    public void onResponse(File response) {
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

    private void downloadAlbum(String picUrl) {
        OkHttpUtils.get().url(picUrl).build()
                .execute(new BitmapCallback() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        music.setCover(bitmap);
                        checkCounter();
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        checkCounter();
                    }
                });
    }
}
