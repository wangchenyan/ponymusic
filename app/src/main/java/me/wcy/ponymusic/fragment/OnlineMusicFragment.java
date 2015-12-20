package me.wcy.ponymusic.fragment;

import android.content.Intent;
import android.content.res.TypedArray;
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
import me.wcy.ponymusic.activity.OnlineMusicListActivity;
import me.wcy.ponymusic.adapter.OnlineMusicAdapter;
import me.wcy.ponymusic.model.OnlineMusicListInfo;
import me.wcy.ponymusic.utils.Constants;

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
        TypedArray icons = getResources().obtainTypedArray(R.array.online_music_list_icon);
        String[] titles = getResources().getStringArray(R.array.online_music_list_title);
        String[] types = getResources().getStringArray(R.array.online_music_list_type);
        for (int i = 0; i < icons.length(); i++) {
            OnlineMusicListInfo info = new OnlineMusicListInfo();
            info.setIcon(icons.getResourceId(i, 0));
            info.setTitle(titles[i]);
            info.setType(types[i]);
            mData.add(info);
        }
        icons.recycle();
        OnlineMusicAdapter mAdapter = new OnlineMusicAdapter(getContext(), mData);
        lvOnlineMusic.setAdapter(mAdapter);
    }

    @Override
    protected void setListener() {
        lvOnlineMusic.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getContext(), OnlineMusicListActivity.class);
        intent.putExtra(Constants.ONLINE_MUSIC_LIST_TYPE, mData.get(position));
        startActivity(intent);
    }
}
