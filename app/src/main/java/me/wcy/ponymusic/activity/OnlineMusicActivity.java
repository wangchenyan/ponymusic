package me.wcy.ponymusic.activity;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.squareup.okhttp.Request;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.BitmapCallback;
import com.zhy.http.okhttp.callback.FileCallBack;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import me.wcy.ponymusic.R;
import me.wcy.ponymusic.adapter.OnMoreClickListener;
import me.wcy.ponymusic.adapter.OnlineMusicAdapter;
import me.wcy.ponymusic.callback.JsonCallback;
import me.wcy.ponymusic.enums.MusicTypeEnum;
import me.wcy.ponymusic.model.JDownloadMusic;
import me.wcy.ponymusic.model.JOnlineMusic;
import me.wcy.ponymusic.model.JOnlineMusicList;
import me.wcy.ponymusic.model.Music;
import me.wcy.ponymusic.model.OnlineMusicListInfo;
import me.wcy.ponymusic.service.PlayService;
import me.wcy.ponymusic.utils.Constants;
import me.wcy.ponymusic.utils.Extras;
import me.wcy.ponymusic.utils.MusicUtils;
import me.wcy.ponymusic.utils.ToastUtil;

public class OnlineMusicActivity extends BaseActivity implements OnItemClickListener, OnMoreClickListener {
    private static final int MUSIC_LIST_SIZE = 20;
    @Bind(R.id.lv_online_music_list)
    ListView lvOnlineMusic;
    private OnlineMusicListInfo mListInfo;
    private JOnlineMusicList jOnlineMusicList;
    private List<JOnlineMusic> mMusicList;
    private OnlineMusicAdapter mAdapter;
    private PlayService mPlayService;
    private PlayServiceConnection mPlayServiceConnection;
    private ProgressDialog mProgressDialog;
    private int mOffset = 0;
    private int mCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_music);

        mListInfo = (OnlineMusicListInfo) getIntent().getSerializableExtra(Extras.ONLINE_MUSIC_LIST_TYPE);
        setTitle(mListInfo.getTitle());
        mMusicList = new ArrayList<>();
        mAdapter = new OnlineMusicAdapter(this, mMusicList);
        lvOnlineMusic.setAdapter(mAdapter);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(getString(R.string.loading));
        mProgressDialog.show();

        bindService();
    }

    @Override
    protected void setListener() {
        lvOnlineMusic.setOnItemClickListener(this);
        mAdapter.setOnMoreClickListener(this);
    }

    private void bindService() {
        Intent intent = new Intent();
        intent.setClass(this, PlayService.class);
        mPlayServiceConnection = new PlayServiceConnection();
        bindService(intent, mPlayServiceConnection, Context.BIND_AUTO_CREATE);
    }

    private class PlayServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mPlayService = ((PlayService.PlayBinder) service).getService();
            getMusic(mOffset);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    }

    private void getMusic(int offset) {
        OkHttpUtils.get().url(Constants.BASE_URL)
                .addParams("method", Constants.METHOD_GET_MUSIC_LIST)
                .addParams("type", mListInfo.getType())
                .addParams("size", String.valueOf(MUSIC_LIST_SIZE))
                .addParams("offset", String.valueOf(offset))
                .build()
                .execute(new JsonCallback<JOnlineMusicList>(JOnlineMusicList.class) {
                    @Override
                    public void onError(Request request, Exception e) {

                    }

                    @Override
                    public void onResponse(JOnlineMusicList response) {
                        mProgressDialog.cancel();
                        jOnlineMusicList = response;
                        Collections.addAll(mMusicList, response.getSong_list());
                        mAdapter.notifyDataSetChanged();
                    }
                });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        play(position);
    }

    @Override
    public void onMoreClick(int position) {

    }

    private void play(int position) {
        mProgressDialog.show();
        JOnlineMusic jOnlineMusic = mMusicList.get(position);
        mCounter = 0;
        if (TextUtils.isEmpty(jOnlineMusic.getLrclink())) {
            mCounter++;
        }
        String picUrl = TextUtils.isEmpty(jOnlineMusic.getPic_big()) ? TextUtils.isEmpty(jOnlineMusic.getPic_small())
                ? null : jOnlineMusic.getPic_small() : jOnlineMusic.getPic_big();
        if (TextUtils.isEmpty(picUrl)) {
            mCounter++;
        }
        final Music music = new Music();
        music.setType(MusicTypeEnum.ONLINE);
        music.setTitle(jOnlineMusic.getTitle());
        music.setArtist(jOnlineMusic.getArtist_name());
        music.setAlbum(jOnlineMusic.getAlbum_title());
        // 获取歌曲播放链接
        OkHttpUtils.get().url(Constants.BASE_URL)
                .addParams("method", Constants.METHOD_DOWNLOAD_MUSIC)
                .addParams("songid", jOnlineMusic.getSong_id())
                .build()
                .execute(new JsonCallback<JDownloadMusic>(JDownloadMusic.class) {
                    @Override
                    public void onResponse(final JDownloadMusic response) {
                        music.setUri(response.getBitrate().getFile_link());
                        music.setDuration(response.getBitrate().getFile_duration() * 1000);
                        mCounter++;
                        if (mCounter == 3) {
                            onDownloadFinish(music);
                        }
                    }

                    @Override
                    public void onError(Request request, Exception e) {

                    }
                });
        // 下载歌词
        String lrcFileName = jOnlineMusic.getArtist_name() + " - " + jOnlineMusic.getTitle() + Constants.FILENAME_LRC;
        if (!TextUtils.isEmpty(jOnlineMusic.getLrclink())) {
            OkHttpUtils.get().url(jOnlineMusic.getLrclink()).build()
                    .execute(new FileCallBack(MusicUtils.getLrcDir(), lrcFileName) {
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
                            mCounter++;
                            if (mCounter == 3) {
                                onDownloadFinish(music);
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
                                onDownloadFinish(music);
                            }
                        }

                        @Override
                        public void onError(Request request, Exception e) {
                            mCounter++;
                            if (mCounter == 3) {
                                onDownloadFinish(music);
                            }
                        }
                    });
        }
    }

    private void onDownloadFinish(Music music) {
        mProgressDialog.cancel();
        mPlayService.play(music);
        ToastUtil.show(getString(R.string.now_play) + music.getTitle());
    }

    @Override
    protected void onDestroy() {
        unbindService(mPlayServiceConnection);
        super.onDestroy();
    }
}
