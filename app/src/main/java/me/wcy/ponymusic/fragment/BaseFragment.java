package me.wcy.ponymusic.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import butterknife.ButterKnife;

/**
 * Created by wcy on 2015/11/26.
 */
public abstract class BaseFragment extends Fragment {

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        init();
        setListener();
        super.onViewCreated(view, savedInstanceState);
    }

    protected abstract void init();

    protected abstract void setListener();
}
