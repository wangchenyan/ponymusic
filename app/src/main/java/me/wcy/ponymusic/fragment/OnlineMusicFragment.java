package me.wcy.ponymusic.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.wcy.ponymusic.R;

/**
 * 在线音乐
 * Created by wcy on 2015/11/26.
 */
public class OnlineMusicFragment extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_online_music, container, false);
    }

    @Override
    protected void init() {

    }

    @Override
    protected void setListener() {

    }
}
