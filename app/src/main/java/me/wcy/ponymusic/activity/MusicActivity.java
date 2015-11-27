package me.wcy.ponymusic.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.Bind;
import me.wcy.ponymusic.R;
import me.wcy.ponymusic.adapter.FragmentAdapter;
import me.wcy.ponymusic.fragment.LocalMusicFragment;
import me.wcy.ponymusic.fragment.OnlineMusicFragment;
import me.wcy.ponymusic.fragment.PlayingFragment;
import me.wcy.ponymusic.service.PlayService;

public class MusicActivity extends BaseActivity implements View.OnClickListener, PlayService.OnPlayEventListener {
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.tabs)
    TabLayout mTabLayout;
    @Bind(R.id.viewpager)
    ViewPager mViewPager;
    @Bind(R.id.ll_playbar)
    LinearLayout llPlayBar;
    @Bind(R.id.iv_playbar_cover)
    ImageView ivPlayBarCover;
    @Bind(R.id.tv_playbar_title)
    TextView tvPlayBarTitle;
    @Bind(R.id.tv_playbar_artist)
    TextView tvPlayBarArtist;
    @Bind(R.id.iv_playbar_play)
    ImageView ivPlayBarPlay;
    @Bind(R.id.iv_playbar_next)
    ImageView ivPlayBarNext;
    private Fragment mLocalMusicFragment;
    private Fragment mOnlineMusicFragment;
    private Fragment mPlayingFragment;
    private PlayService mPlayService;
    private boolean isPlayingFragmentShow = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        setSupportActionBar(mToolbar);
        setupViewPager();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent();
        intent.setClass(this, PlayService.class);
        PlayServiceConnection conn = new PlayServiceConnection();
        bindService(intent, conn, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void setListener() {
        llPlayBar.setOnClickListener(this);
    }

    private void setupViewPager() {
        mLocalMusicFragment = new LocalMusicFragment();
        mOnlineMusicFragment = new OnlineMusicFragment();
        FragmentAdapter adapter = new FragmentAdapter(getSupportFragmentManager());
        adapter.addFragment(mLocalMusicFragment, "本地音乐");
        adapter.addFragment(mOnlineMusicFragment, "在线音乐");
        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    public void onPublish(int percent) {

    }

    @Override
    public void onChange(int position) {

    }

    private class PlayServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mPlayService = ((PlayService.PlayBinder) service).getService();
            mPlayService.setOnPlayEventListener(MusicActivity.this);
            onChange(mPlayService.getPlayingPosition());
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_playbar:
                showPlayingFragment();
                break;
        }
    }

    private void showPlayingFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (mPlayingFragment == null) {
            mPlayingFragment = new PlayingFragment();
            ft.replace(android.R.id.content, mPlayingFragment);
            ft.addToBackStack(null);
        } else {
            ft.show(mPlayingFragment);
        }
        ft.commit();
        isPlayingFragmentShow = true;
    }

    private void hidePlayingFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.hide(mPlayingFragment);
        ft.commit();
        isPlayingFragmentShow = false;
    }

    @Override
    public void onBackPressed() {
        if (mPlayingFragment != null && isPlayingFragmentShow) {
            hidePlayingFragment();
        } else {
            moveTaskToBack(false);
        }
    }
}
