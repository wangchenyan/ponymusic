package me.wcy.ponymusic.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import butterknife.Bind;
import me.wcy.ponymusic.R;
import me.wcy.ponymusic.adapter.LocalMusicAdapter;

/**
 * Created by wcy on 2015/11/26.
 */
public class LocalMusicFragment extends BaseFragment {
    @Bind(R.id.lv_local_music)
    ListView lvLocalMusic;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_local_music, container, false);
    }

    @Override
    protected void init() {
        LocalMusicAdapter adapter = new LocalMusicAdapter(getContext());
        lvLocalMusic.setAdapter(adapter);
    }

    @Override
    protected void setListener() {

    }
}
