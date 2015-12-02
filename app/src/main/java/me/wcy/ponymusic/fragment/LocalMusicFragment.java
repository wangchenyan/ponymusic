package me.wcy.ponymusic.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import butterknife.Bind;
import me.wcy.ponymusic.R;
import me.wcy.ponymusic.adapter.LocalMusicAdapter;
import me.wcy.ponymusic.utils.MusicUtils;

/**
 * 本地音乐列表
 * Created by wcy on 2015/11/26.
 */
public class LocalMusicFragment extends BaseFragment implements AdapterView.OnItemClickListener, LocalMusicAdapter.OnMoreClickListener {
    @Bind(R.id.lv_local_music)
    ListView lvLocalMusic;
    @Bind(R.id.tv_empty)
    TextView tvEmpty;
    private LocalMusicAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_local_music, container, false);
    }

    @Override
    protected void init() {
        if (MusicUtils.getMusicList().isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            return;
        }
        int playingPosition = getPlayService().getPlayingPosition();
        mAdapter = new LocalMusicAdapter(getActivity(), playingPosition);
        mAdapter.setOnMoreClickListener(this);
        lvLocalMusic.setAdapter(mAdapter);
        lvLocalMusic.setSelection(playingPosition);
    }

    @Override
    protected void setListener() {
        lvLocalMusic.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        getPlayService().play(position);
    }

    @Override
    public void onMoreClick(int position) {
    }

    public void onItemPlay(int position) {
        mAdapter.setPlayingPosition(position);
        mAdapter.notifyDataSetChanged();
        lvLocalMusic.smoothScrollToPosition(position);
    }
}
