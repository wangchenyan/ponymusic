package me.wcy.music.executor;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.BitmapCallback;
import com.zhy.http.okhttp.callback.FileCallBack;

import java.io.File;

import me.wcy.music.R;
import me.wcy.music.callback.JsonCallback;
import me.wcy.music.model.JDownloadInfo;
import me.wcy.music.model.JOnlineMusic;
import me.wcy.music.model.Music;
import me.wcy.music.constants.Constants;
import me.wcy.music.utils.FileUtils;
import me.wcy.music.utils.NetworkUtils;
import me.wcy.music.utils.Preferences;
import okhttp3.Call;

/**
 * 播放在线音乐
 * Created by wcy on 2016/1/3.
 */
public abstract class PlayOnlineMusic {
    private Context mContext;
    private JOnlineMusic mJOnlineMusic;
    private int mCounter = 0;

    public PlayOnlineMusic(Context context, JOnlineMusic jOnlineMusic) {
        mContext = context;
        mJOnlineMusic = jOnlineMusic;
    }

    public void execute() {
        checkNetwork();
    }

    private void checkNetwork() {
        boolean mobileNetworkPlay = Preferences.enableMobileNetworkPlay();
        if (NetworkUtils.isActiveNetworkMobile(mContext) && !mobileNetworkPlay) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle(R.string.tips);
            builder.setMessage(R.string.play_tips);
            builder.setPositiveButton(R.string.play_tips_sure, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Preferences.saveMobileNetworkPlay(true);
                    getPlayInfo();
                }
            });
            builder.setNegativeButton(R.string.cancel, null);
            Dialog dialog = builder.create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        } else {
            getPlayInfo();
        }
    }

    private void getPlayInfo() {
        onPrepare();
        String lrcFileName = FileUtils.getLrcFileName(mJOnlineMusic.getArtist_name(), mJOnlineMusic.getTitle());
        File lrcFile = new File(FileUtils.getLrcDir() + lrcFileName);
        if (TextUtils.isEmpty(mJOnlineMusic.getLrclink()) || lrcFile.exists()) {
            mCounter++;
        }
        String picUrl = TextUtils.isEmpty(mJOnlineMusic.getPic_big()) ? TextUtils.isEmpty(mJOnlineMusic.getPic_small())
                ? null : mJOnlineMusic.getPic_small() : mJOnlineMusic.getPic_big();
        if (TextUtils.isEmpty(picUrl)) {
            mCounter++;
        }
        final Music music = new Music();
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
                        if (response == null) {
                            onFail(null, null);
                            return;
                        }
                        music.setUri(response.getBitrate().getFile_link());
                        music.setDuration(response.getBitrate().getFile_duration() * 1000);
                        mCounter++;
                        if (mCounter == 3) {
                            onSuccess(music);
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        onFail(call, e);
                    }
                });
        // 下载歌词
        if (!TextUtils.isEmpty(mJOnlineMusic.getLrclink()) && !lrcFile.exists()) {
            OkHttpUtils.get().url(mJOnlineMusic.getLrclink()).build()
                    .execute(new FileCallBack(FileUtils.getLrcDir(), lrcFileName) {
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
                            mCounter++;
                            if (mCounter == 3) {
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
                            mCounter++;
                            if (mCounter == 3) {
                                onSuccess(music);
                            }
                        }

                        @Override
                        public void onError(Call call, Exception e) {
                            mCounter++;
                            if (mCounter == 3) {
                                onSuccess(music);
                            }
                        }
                    });
        }
    }

    public abstract void onPrepare();

    public abstract void onSuccess(Music music);

    public abstract void onFail(Call call, Exception e);
}
