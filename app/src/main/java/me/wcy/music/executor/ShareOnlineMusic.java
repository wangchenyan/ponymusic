package me.wcy.music.executor;

import android.content.Context;
import android.content.Intent;

import com.zhy.http.okhttp.OkHttpUtils;

import me.wcy.music.R;
import me.wcy.music.callback.JsonCallback;
import me.wcy.music.model.JDownloadInfo;
import me.wcy.music.utils.Constants;
import me.wcy.music.utils.ToastUtils;
import okhttp3.Call;

/**
 * 分享在线歌曲
 * Created by hzwangchenyan on 2016/1/13.
 */
public abstract class ShareOnlineMusic {
    private Context mContext;
    private String mTitle;
    private String mSongId;

    public ShareOnlineMusic(Context context, String title, String songId) {
        mContext = context;
        mTitle = title;
        mSongId = songId;
    }

    public void execute() {
        share();
    }

    private void share() {
        onPrepare();
        // 获取歌曲播放链接
        OkHttpUtils.get().url(Constants.BASE_URL)
                .addParams(Constants.PARAM_METHOD, Constants.METHOD_DOWNLOAD_MUSIC)
                .addParams(Constants.PARAM_SONG_ID, mSongId)
                .build()
                .execute(new JsonCallback<JDownloadInfo>(JDownloadInfo.class) {
                    @Override
                    public void onResponse(final JDownloadInfo response) {
                        if (response == null) {
                            onFail(null, null);
                            ToastUtils.show(R.string.unable_to_share);
                            return;
                        }
                        onSuccess();
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("text/plain");
                        intent.putExtra(Intent.EXTRA_TEXT, mContext.getString(R.string.share_music, mContext.getString(R.string.app_name),
                                mTitle, response.getBitrate().getFile_link()));
                        mContext.startActivity(Intent.createChooser(intent, mContext.getString(R.string.share)));
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        onFail(call, e);
                        ToastUtils.show(R.string.unable_to_share);
                    }
                });
    }

    public abstract void onPrepare();

    public abstract void onSuccess();

    public abstract void onFail(Call call, Exception e);
}
