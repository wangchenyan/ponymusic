package me.wcy.music.executor;

import android.text.TextUtils;

import com.zhy.http.okhttp.OkHttpUtils;

import me.wcy.music.callback.JsonCallback;
import me.wcy.music.constants.Constants;
import me.wcy.music.model.JLrc;
import me.wcy.music.model.JSearchMusic;
import me.wcy.music.utils.FileUtils;
import okhttp3.Call;

/**
 * 如果本地歌曲没有歌词则从网络搜索歌词
 * Created by wcy on 2016/4/26.
 */
public abstract class SearchLrc implements IExecutor<String> {
    private String artist;
    private String title;

    public SearchLrc(String artist, String title) {
        this.artist = artist;
        this.title = title;
    }

    @Override
    public void execute() {
        onPrepare();
        searchLrc();
    }

    private void searchLrc() {
        OkHttpUtils.get().url(Constants.BASE_URL)
                .addParams(Constants.PARAM_METHOD, Constants.METHOD_SEARCH_MUSIC)
                .addParams(Constants.PARAM_QUERY, title + "-" + artist)
                .build()
                .execute(new JsonCallback<JSearchMusic>(JSearchMusic.class) {
                    @Override
                    public void onResponse(JSearchMusic response) {
                        if (response == null || response.getSong() == null || response.getSong().isEmpty()) {
                            onFail(null);
                            return;
                        }

                        downloadLrc(response.getSong().get(0).getSongid());
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        onFail(e);
                    }
                });
    }

    private void downloadLrc(String songId) {
        OkHttpUtils.get().url(Constants.BASE_URL)
                .addParams(Constants.PARAM_METHOD, Constants.METHOD_LRC)
                .addParams(Constants.PARAM_SONG_ID, songId)
                .build()
                .execute(new JsonCallback<JLrc>(JLrc.class) {
                    @Override
                    public void onResponse(JLrc response) {
                        if (response == null || TextUtils.isEmpty(response.getLrcContent())) {
                            onFail(null);
                            return;
                        }

                        String filePath = FileUtils.getLrcDir() + FileUtils.getLrcFileName(artist, title);
                        FileUtils.saveLrcFile(filePath, response.getLrcContent());
                        onSuccess(filePath);
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        onFail(e);
                    }
                });
    }
}
