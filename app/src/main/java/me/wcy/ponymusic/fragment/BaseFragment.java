package me.wcy.ponymusic.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import butterknife.ButterKnife;
import me.wcy.ponymusic.activity.MusicActivity;

/**
 * Created by wcy on 2015/11/26.
 */
public abstract class BaseFragment extends Fragment {
    protected MusicActivity mActivity;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        init();
        setListener();
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (MusicActivity) activity;
    }

    protected abstract void init();

    protected abstract void setListener();
}
