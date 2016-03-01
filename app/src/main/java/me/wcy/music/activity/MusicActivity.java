package me.wcy.music.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import butterknife.Bind;
import me.wcy.music.R;
import me.wcy.music.adapter.FragmentAdapter;
import me.wcy.music.executor.NaviMenuExecutor;
import me.wcy.music.executor.WeatherExecutor;
import me.wcy.music.fragment.LocalMusicFragment;
import me.wcy.music.fragment.PlayFragment;
import me.wcy.music.fragment.SongListFragment;
import me.wcy.music.model.Music;
import me.wcy.music.receiver.RemoteControlReceiver;
import me.wcy.music.service.OnPlayerEventListener;
import me.wcy.music.service.PlayService;
import me.wcy.music.utils.CoverLoader;

public class MusicActivity extends BaseActivity implements View.OnClickListener, OnPlayerEventListener,
        NavigationView.OnNavigationItemSelectedListener, ViewPager.OnPageChangeListener {
    @Bind(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @Bind(R.id.navigation_view)
    NavigationView navigationView;
    @Bind(R.id.iv_menu)
    ImageView ivMenu;
    @Bind(R.id.tv_local_music)
    TextView tvLocalMusic;
    @Bind(R.id.tv_online_music)
    TextView tvOnlineMusic;
    @Bind(R.id.viewpager)
    ViewPager mViewPager;
    @Bind(R.id.fl_play_bar)
    FrameLayout flPlayBar;
    @Bind(R.id.iv_play_bar_cover)
    ImageView ivPlayBarCover;
    @Bind(R.id.tv_play_bar_title)
    TextView tvPlayBarTitle;
    @Bind(R.id.tv_play_bar_artist)
    TextView tvPlayBarArtist;
    @Bind(R.id.iv_play_bar_play)
    ImageView ivPlayBarPlay;
    @Bind(R.id.iv_play_bar_next)
    ImageView ivPlayBarNext;
    @Bind(R.id.pb_play_bar)
    ProgressBar mProgressBar;
    private View vNavigationHeader;
    private LocalMusicFragment mLocalMusicFragment;
    private SongListFragment mSongListFragment;
    private PlayFragment mPlayFragment;
    private PlayService mPlayService;
    private AudioManager mAudioManager;
    private ComponentName mRemoteReceiver;
    private boolean mIsPlayFragmentShow = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        bindService();
    }

    @Override
    protected void onDestroy() {
        unbindService(mPlayServiceConnection);
        mAudioManager.unregisterMediaButtonEventReceiver(mRemoteReceiver);
        super.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                showPlayingFragment();
            }
        }, 100);
    }

    private void bindService() {
        Intent intent = new Intent();
        intent.setClass(this, PlayService.class);
        bindService(intent, mPlayServiceConnection, Context.BIND_AUTO_CREATE);
    }

    private ServiceConnection mPlayServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mPlayService = ((PlayService.PlayBinder) service).getService();
            mPlayService.setOnPlayEventListener(MusicActivity.this);
            init();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    private void init() {
        setupView();
        updateWeather();
        registerReceiver();
        onChange(mPlayService.getPlayingMusic());
    }

    @Override
    protected void setListener() {
        ivMenu.setOnClickListener(this);
        tvLocalMusic.setOnClickListener(this);
        tvOnlineMusic.setOnClickListener(this);
        mViewPager.setOnPageChangeListener(this);
        flPlayBar.setOnClickListener(this);
        ivPlayBarPlay.setOnClickListener(this);
        ivPlayBarNext.setOnClickListener(this);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void setupView() {
        // add navigation header
        vNavigationHeader = LayoutInflater.from(this).inflate(R.layout.navigation_header, null);
        navigationView.addHeaderView(vNavigationHeader);

        // setup view pager
        mLocalMusicFragment = new LocalMusicFragment();
        mSongListFragment = new SongListFragment();
        FragmentAdapter adapter = new FragmentAdapter(getSupportFragmentManager());
        adapter.addFragment(mLocalMusicFragment);
        adapter.addFragment(mSongListFragment);
        mViewPager.setAdapter(adapter);
        tvLocalMusic.setSelected(true);
    }

    private void updateWeather() {
        new WeatherExecutor(this, vNavigationHeader).execute();
    }

    private void registerReceiver() {
        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        mRemoteReceiver = new ComponentName(getPackageName(), RemoteControlReceiver.class.getName());
        mAudioManager.registerMediaButtonEventReceiver(mRemoteReceiver);
    }

    /**
     * 更新播放进度
     */
    @Override
    public void onPublish(int progress) {
        mProgressBar.setProgress(progress);
        if (mPlayFragment != null && mPlayFragment.isResume()) {
            mPlayFragment.onPublish(progress);
        }
    }

    @Override
    public void onChange(Music music) {
        onPlay(music);
        if (mPlayFragment != null && mPlayFragment.isResume()) {
            mPlayFragment.onChange(music);
        }
    }

    @Override
    public void onPlayerPause() {
        ivPlayBarPlay.setSelected(false);
        if (mPlayFragment != null && mPlayFragment.isResume()) {
            mPlayFragment.onPlayerPause();
        }
    }

    @Override
    public void onPlayerResume() {
        ivPlayBarPlay.setSelected(true);
        if (mPlayFragment != null && mPlayFragment.isResume()) {
            mPlayFragment.onPlayerResume();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_menu:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.tv_local_music:
                mViewPager.setCurrentItem(0);
                break;
            case R.id.tv_online_music:
                mViewPager.setCurrentItem(1);
                break;
            case R.id.fl_play_bar:
                showPlayingFragment();
                break;
            case R.id.iv_play_bar_play:
                play();
                break;
            case R.id.iv_play_bar_next:
                next();
                break;
        }
    }

    @Override
    public boolean onNavigationItemSelected(final MenuItem item) {
        drawerLayout.closeDrawers();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                item.setChecked(false);
            }
        }, 500);
        return NaviMenuExecutor.getInstance().setContext(this).onNavigationItemSelected(item);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        if (position == 0) {
            tvLocalMusic.setSelected(true);
            tvOnlineMusic.setSelected(false);
        } else {
            tvLocalMusic.setSelected(false);
            tvOnlineMusic.setSelected(true);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    public void onPlay(Music music) {
        if (music == null) {
            return;
        }
        Bitmap cover;
        if (music.getCover() == null) {
            cover = CoverLoader.getInstance().loadThumbnail(music.getCoverUri());
        } else {
            cover = music.getCover();
        }
        ivPlayBarCover.setImageBitmap(cover);
        tvPlayBarTitle.setText(music.getTitle());
        tvPlayBarArtist.setText(music.getArtist());
        if (getPlayService().isPlaying()) {
            ivPlayBarPlay.setSelected(true);
        } else {
            ivPlayBarPlay.setSelected(false);
        }
        mProgressBar.setMax((int) music.getDuration());
        mProgressBar.setProgress(0);

        if (mLocalMusicFragment != null && mLocalMusicFragment.isResume()) {
            mLocalMusicFragment.onItemPlay();
        }
    }

    private void play() {
        getPlayService().playPause();
    }

    private void next() {
        getPlayService().next();
    }

    public PlayService getPlayService() {
        return mPlayService;
    }

    private void showPlayingFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.fragment_slide_up, 0);
        if (mPlayFragment == null) {
            mPlayFragment = new PlayFragment();
            ft.replace(android.R.id.content, mPlayFragment);
        } else {
            ft.show(mPlayFragment);
        }
        ft.commit();
        mIsPlayFragmentShow = true;
    }

    private void hidePlayingFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(0, R.anim.fragment_slide_down);
        ft.hide(mPlayFragment);
        ft.commit();
        mIsPlayFragmentShow = false;
    }

    @Override
    public void onBackPressed() {
        if (mPlayFragment != null && mIsPlayFragmentShow) {
            hidePlayingFragment();
            return;
        }
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers();
            return;
        }
        moveTaskToBack(false);
    }
}
