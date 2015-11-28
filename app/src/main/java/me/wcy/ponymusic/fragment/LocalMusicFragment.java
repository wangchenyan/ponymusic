package me.wcy.ponymusic.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import butterknife.Bind;
import me.wcy.ponymusic.R;
import me.wcy.ponymusic.adapter.LocalMusicAdapter;

/**
 * Created by wcy on 2015/11/26.
 */
public class LocalMusicFragment extends BaseFragment implements AdapterView.OnItemClickListener {
    @Bind(R.id.lv_local_music)
    ListView lvLocalMusic;
    private LocalMusicAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_local_music, container, false);
    }

    @Override
    protected void init() {
        int playingPosition;
        if (mActivity.getPlayService() != null) {
            playingPosition = mActivity.getPlayService().getPlayingPosition();
        } else {
            playingPosition = -1;
        }
        adapter = new LocalMusicAdapter(getContext(), playingPosition);
        lvLocalMusic.setAdapter(adapter);
    }

    @Override
    protected void setListener() {
        lvLocalMusic.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mActivity.getPlayService().play(position);
    }

    public void onItemPlay(int position) {
        if (adapter != null) {
            adapter.setPlayingPosition(position);
            adapter.notifyDataSetChanged();
        }
    }
}
