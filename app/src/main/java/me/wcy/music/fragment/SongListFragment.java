package me.wcy.music.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import me.wcy.music.R;
import me.wcy.music.activity.OnlineMusicActivity;
import me.wcy.music.adapter.SongListAdapter;
import me.wcy.music.enums.LoadStateEnum;
import me.wcy.music.model.SongListInfo;
import me.wcy.music.utils.Extras;
import me.wcy.music.utils.NetworkUtils;
import me.wcy.music.utils.ViewUtils;

/**
 * 在线音乐
 * Created by wcy on 2015/11/26.
 */
public class SongListFragment extends BaseFragment implements AdapterView.OnItemClickListener {
    @Bind(R.id.lv_song_list)
    ListView lvSongList;
    @Bind(R.id.ll_loading)
    LinearLayout llLoading;
    @Bind(R.id.ll_load_fail)
    LinearLayout llLoadFail;
    private List<SongListInfo> mData;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_song_list, container, false);
    }

    @Override
    protected void init() {
        if (!NetworkUtils.isNetworkAvailable(getActivity())) {
            ViewUtils.changeViewState(lvSongList, llLoading, llLoadFail, LoadStateEnum.LOAD_FAIL);
            return;
        }
        mData = new ArrayList<>();
        String[] titles = getResources().getStringArray(R.array.online_music_list_title);
        String[] types = getResources().getStringArray(R.array.online_music_list_type);
        for (int i = 0; i < titles.length; i++) {
            SongListInfo info = new SongListInfo();
            info.setTitle(titles[i]);
            info.setType(types[i]);
            mData.add(info);
        }
        SongListAdapter mAdapter = new SongListAdapter(getActivity(), mData);
        lvSongList.setAdapter(mAdapter);
    }

    @Override
    protected void setListener() {
        lvSongList.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        SongListInfo songListInfo = mData.get(position);
        Intent intent = new Intent(getActivity(), OnlineMusicActivity.class);
        intent.putExtra(Extras.MUSIC_LIST_TYPE, songListInfo);
        startActivity(intent);
    }
}
