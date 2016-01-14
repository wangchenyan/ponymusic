package me.wcy.music.online;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import com.squareup.okhttp.Request;
import com.zhy.http.okhttp.OkHttpUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import me.wcy.music.callback.JsonCallback;
import me.wcy.music.model.JDownloadInfo;
import me.wcy.music.model.JLrc;
import me.wcy.music.model.JSearchMusic;
import me.wcy.music.utils.Constants;
import me.wcy.music.utils.FileUtils;
import me.wcy.music.utils.Preferences;

/**
 * 下载搜索的音乐
 * Created by hzwangchenyan on 2016/1/13.
 */
public abstract class DownloadSearchedMusic {
    private Context mContext;
    private JSearchMusic.JSong mJSong;

    public DownloadSearchedMusic(Context context, JSearchMusic.JSong jSong) {
        mContext = context;
        mJSong = jSong;
    }

    public void execute() {
        download();
    }

    private void download() {
        onPrepare();
        final DownloadManager downloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
        // 获取歌曲下载链接
        OkHttpUtils.get().url(Constants.BASE_URL)
                .addParams(Constants.PARAM_METHOD, Constants.METHOD_DOWNLOAD_MUSIC)
                .addParams(Constants.PARAM_SONG_ID, mJSong.getSongid())
                .build()
                .execute(new JsonCallback<JDownloadInfo>(JDownloadInfo.class) {
                    @Override
                    public void onResponse(final JDownloadInfo response) {
                        Uri uri = Uri.parse(response.getBitrate().getFile_link());
                        DownloadManager.Request request = new DownloadManager.Request(uri);
                        String mp3FileName = FileUtils.getMp3FileName(mJSong.getArtistname(), mJSong.getSongname());
                        request.setDestinationInExternalPublicDir(FileUtils.getRelativeMusicDir(), mp3FileName);
                        request.setMimeType(MimeTypeMap.getFileExtensionFromUrl(response.getBitrate().getFile_link()));
                        request.allowScanningByMediaScanner();
                        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
                        request.setAllowedOverRoaming(false);// 不允许漫游
                        long id = downloadManager.enqueue(request);
                        Preferences.put(mContext, String.valueOf(id), mJSong.getSongname());
                        onSuccess();
                    }

                    @Override
                    public void onError(Request request, Exception e) {
                        onFail(request, e);
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
                            if (TextUtils.isEmpty(response.getLrcContent())) {
                                return;
                            }
                            String lrcFileName = FileUtils.getLrcFileName(mJSong.getArtistname(), mJSong.getSongname());
                            saveLrcFile(lrcFileName, response.getLrcContent());
                        }

                        @Override
                        public void onError(Request request, Exception e) {
                        }
                    });
        }
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

    public abstract void onSuccess();

    public abstract void onFail(Request request, Exception e);
}
