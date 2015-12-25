package me.wcy.ponymusic.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import me.wcy.ponymusic.R;
import me.wcy.ponymusic.activity.OnlineMusicActivity;
import me.wcy.ponymusic.adapter.OnlineMusicListAdapter;
import me.wcy.ponymusic.model.OnlineMusicListInfo;
import me.wcy.ponymusic.utils.Extras;

/**
 * 在线音乐
 * Created by wcy on 2015/11/26.
 */
public class OnlineMusicFragment extends BaseFragment implements AdapterView.OnItemClickListener {
    @Bind(R.id.lv_online_music)
    ListView lvOnlineMusic;
    private List<OnlineMusicListInfo> mData;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_online_music, container, false);
    }

    @Override
    protected void init() {
        mData = new ArrayList<>();
        String[] titles = getResources().getStringArray(R.array.online_music_list_title);
        String[] types = getResources().getStringArray(R.array.online_music_list_type);
        for (int i = 0; i < titles.length; i++) {
            OnlineMusicListInfo info = new OnlineMusicListInfo();
            info.setTitle(titles[i]);
            info.setType(types[i]);
            mData.add(info);
        }
        OnlineMusicListAdapter mAdapter = new OnlineMusicListAdapter(getContext(), mData);
        lvOnlineMusic.setAdapter(mAdapter);
    }

    @Override
    protected void setListener() {
        lvOnlineMusic.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        OnlineMusicListInfo musicListInfo = mData.get(position);
        if (musicListInfo.getType().equals("*")) {
            return;
        }
        Intent intent = new Intent(getContext(), OnlineMusicActivity.class);
        intent.putExtra(Extras.ONLINE_MUSIC_LIST_TYPE, musicListInfo);
        startActivity(intent);
    }
}
