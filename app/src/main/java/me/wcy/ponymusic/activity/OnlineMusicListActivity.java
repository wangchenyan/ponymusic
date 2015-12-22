package me.wcy.ponymusic.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.squareup.okhttp.Request;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import me.wcy.ponymusic.R;
import me.wcy.ponymusic.adapter.OnMoreClickListener;
import me.wcy.ponymusic.adapter.OnlineMusicListAdapter;
import me.wcy.ponymusic.model.OnlineMusic;
import me.wcy.ponymusic.model.OnlineMusicList;
import me.wcy.ponymusic.model.OnlineMusicListInfo;
import me.wcy.ponymusic.utils.Constants;
import me.wcy.ponymusic.utils.JsonCallback;

public class OnlineMusicListActivity extends BaseActivity implements OnItemClickListener, OnMoreClickListener {
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.lv_online_music_list)
    ListView lvOnlineMusic;
    private OnlineMusicListInfo mListInfo;
    private OnlineMusicList mOnlineMusicList;
    private List<OnlineMusic> mMusicList;
    private OnlineMusicListAdapter mAdapter;
    private int mOffset = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_music_list);

        setSupportActionBar(mToolbar);
        mListInfo = (OnlineMusicListInfo) getIntent().getSerializableExtra(Constants.ONLINE_MUSIC_LIST_TYPE);
        mMusicList = new ArrayList<>();
        mAdapter = new OnlineMusicListAdapter(this, mMusicList);
        lvOnlineMusic.setAdapter(mAdapter);
        lvOnlineMusic.setOnItemClickListener(this);
        mAdapter.setOnMoreClickListener(this);

        getMusic(mOffset);
    }

    @Override
    protected void setListener() {

    }

    private void getMusic(int offset) {
        OkHttpUtils.get().url(Constants.BASE_URL)
                .addParams("method", Constants.METHOD_GET_MUSIC_LIST)
                .addParams("type", mListInfo.getType())
                .addParams("size", "10")
                .addParams("offset", String.valueOf(offset))
                .build()
                .execute(new JsonCallback<OnlineMusicList>(OnlineMusicList.class) {
                    @Override
                    public void onError(Request request, Exception e) {

                    }

                    @Override
                    public void onResponse(OnlineMusicList response) {
                        mOnlineMusicList = response;
                        Collections.addAll(mMusicList, response.getSong_list());
                        mAdapter.notifyDataSetChanged();
                    }
                });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onMoreClick(int position) {

    }
}
