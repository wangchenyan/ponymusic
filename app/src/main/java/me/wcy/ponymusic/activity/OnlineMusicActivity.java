package me.wcy.ponymusic.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.squareup.okhttp.Request;
import com.zhy.http.okhttp.OkHttpUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import me.wcy.ponymusic.R;
import me.wcy.ponymusic.adapter.OnMoreClickListener;
import me.wcy.ponymusic.adapter.OnlineMusicAdapter;
import me.wcy.ponymusic.callback.JsonCallback;
import me.wcy.ponymusic.enums.LoadStateEnum;
import me.wcy.ponymusic.model.JOnlineMusic;
import me.wcy.ponymusic.model.JOnlineMusicList;
import me.wcy.ponymusic.model.Music;
import me.wcy.ponymusic.model.MusicListInfo;
import me.wcy.ponymusic.service.PlayService;
import me.wcy.ponymusic.utils.Constants;
import me.wcy.ponymusic.utils.DownloadMusic;
import me.wcy.ponymusic.utils.Extras;
import me.wcy.ponymusic.utils.FileUtils;
import me.wcy.ponymusic.utils.MusicUtils;
import me.wcy.ponymusic.utils.PlayMusic;
import me.wcy.ponymusic.utils.ToastUtils;

public class OnlineMusicActivity extends BaseActivity implements OnItemClickListener, OnMoreClickListener {
    @Bind(R.id.lv_online_music_list)
    ListView lvOnlineMusic;
    @Bind(R.id.ll_loading)
    LinearLayout llLoading;
    @Bind(R.id.ll_load_fail)
    LinearLayout llLoadFail;
    private MusicListInfo mListInfo;
    private JOnlineMusicList jOnlineMusicList;
    private List<JOnlineMusic> mMusicList;
    private OnlineMusicAdapter mAdapter;
    private PlayService mPlayService;
    private PlayServiceConnection mPlayServiceConnection;
    private ProgressDialog mProgressDialog;
    private int mOffset = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_music);

        mListInfo = (MusicListInfo) getIntent().getSerializableExtra(Extras.MUSIC_LIST_TYPE);
        setTitle(mListInfo.getTitle());
        mMusicList = new ArrayList<>();
        mAdapter = new OnlineMusicAdapter(this, mMusicList);
        lvOnlineMusic.setAdapter(mAdapter);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(getString(R.string.loading));
        MusicUtils.changeViewState(lvOnlineMusic, llLoading, llLoadFail, LoadStateEnum.LOADING);

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
                .addParams("size", String.valueOf(Constants.MUSIC_LIST_SIZE))
                .addParams("offset", String.valueOf(offset))
                .build()
                .execute(new JsonCallback<JOnlineMusicList>(JOnlineMusicList.class) {
                    @Override
                    public void onResponse(JOnlineMusicList response) {
                        MusicUtils.changeViewState(lvOnlineMusic, llLoading, llLoadFail, LoadStateEnum.LOAD_SUCCESS);
                        jOnlineMusicList = response;
                        Collections.addAll(mMusicList, response.getSong_list());
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Request request, Exception e) {
                        MusicUtils.changeViewState(lvOnlineMusic, llLoading, llLoadFail, LoadStateEnum.LOAD_FAIL);
                    }
                });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        play(mMusicList.get(position));
    }

    @Override
    public void onMoreClick(int position) {
        final JOnlineMusic jOnlineMusic = mMusicList.get(position);
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(mMusicList.get(position).getTitle());
        String path = FileUtils.getMusicDir() + FileUtils.getMp3FileName(jOnlineMusic.getArtist_name(), jOnlineMusic.getTitle());
        File file = new File(path);
        int itemsId = file.exists() ? R.array.online_music_dialog_no_download : R.array.online_music_dialog;
        dialog.setItems(itemsId, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:// 查看歌手信息
                        break;
                    case 1:// 分享
                        break;
                    case 2:// 下载
                        download(jOnlineMusic);
                        break;
                }
            }
        });
        dialog.show();
    }

    private void play(JOnlineMusic jOnlineMusic) {
        new PlayMusic(jOnlineMusic) {

            @Override
            public void onPrepare() {
                mProgressDialog.show();
            }

            @Override
            public void onSuccess(Music music) {
                mProgressDialog.cancel();
                mPlayService.play(music);
                ToastUtils.show(getString(R.string.now_play) + music.getTitle());
            }

            @Override
            public void onFail(Request request, Exception e) {
                ToastUtils.show("暂时无法播放");
            }
        }.execute();
    }

    private void download(final JOnlineMusic jOnlineMusic) {
        new DownloadMusic(this, jOnlineMusic) {
            @Override
            public void onPrepare() {
                mProgressDialog.show();
            }

            @Override
            public void onSuccess() {
                mProgressDialog.cancel();
                ToastUtils.show(getString(R.string.now_download) + jOnlineMusic.getTitle());
            }

            @Override
            public void onFail(Request request, Exception e) {
                mProgressDialog.cancel();
                ToastUtils.show("暂时无法下载");
            }
        }.execute();
    }

    @Override
    protected void onDestroy() {
        unbindService(mPlayServiceConnection);
        super.onDestroy();
    }
}
