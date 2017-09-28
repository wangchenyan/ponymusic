package me.wcy.music.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import me.wcy.music.application.AppCache;
import me.wcy.music.service.PlayService;
import me.wcy.music.utils.PermissionReq;
import me.wcy.music.utils.binding.ViewBinder;

/**
 * 基类<br>
 * Created by wcy on 2015/11/26.
 */
public abstract class BaseFragment extends Fragment {
    protected Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ViewBinder.bind(this, getView());
    }

    @Override
    public void onStart() {
        super.onStart();
        setListener();
    }

    protected void setListener() {
    }

    protected PlayService getPlayService() {
        PlayService playService = AppCache.getPlayService();
        if (playService == null) {
            throw new NullPointerException("play service is null");
        }
        return playService;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionReq.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
