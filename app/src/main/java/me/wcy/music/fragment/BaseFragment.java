package me.wcy.music.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import me.wcy.music.activity.MusicActivity;
import me.wcy.music.service.PlayService;
import me.wcy.music.utils.binding.ViewBinder;

/**
 * 基类
 * Created by wcy on 2015/11/26.
 */
public abstract class BaseFragment extends Fragment {
    private PlayService mPlayService;
    protected Handler mHandler;
    private boolean isInitialized;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new Handler();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof MusicActivity) {
            mPlayService = ((MusicActivity) activity).getPlayService();
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ViewBinder.bind(this, view);
        init();
        setListener();

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                isInitialized = true;
            }
        });
    }

    protected abstract void init();

    protected abstract void setListener();

    public boolean isInitialized() {
        return isInitialized;
    }

    protected PlayService getPlayService() {
        return mPlayService;
    }
}
