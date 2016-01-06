package me.wcy.ponymusic.fragment;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import me.wcy.ponymusic.R;
import me.wcy.ponymusic.adapter.PlayPagerAdapter;
import me.wcy.ponymusic.enums.MusicTypeEnum;
import me.wcy.ponymusic.enums.PlayModeEnum;
import me.wcy.ponymusic.model.Music;
import me.wcy.ponymusic.utils.Constants;
import me.wcy.ponymusic.utils.CoverLoader;
import me.wcy.ponymusic.utils.FileUtils;
import me.wcy.ponymusic.utils.ImageUtils;
import me.wcy.ponymusic.utils.Preferences;
import me.wcy.ponymusic.utils.ToastUtils;
import me.wcy.ponymusic.utils.Utils;
import me.wcy.ponymusic.widget.AlbumCoverView;
import me.wcy.ponymusic.widget.IndicatorLayout;
import me.wcy.ponymusic.widget.LrcView;

/**
 * 正在播放界面
 * Created by wcy on 2015/11/27.
 */
public class PlayFragment extends BaseFragment implements View.OnClickListener, ViewPager.OnPageChangeListener, SeekBar.OnSeekBarChangeListener {
    @Bind(R.id.ll_content)
    LinearLayout llContent;
    @Bind(R.id.iv_play_page_bg)
    ImageView ivPlayingBg;
    @Bind(R.id.iv_back)
    ImageView ivBack;
    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.tv_artist)
    TextView tvArtist;
    @Bind(R.id.vp_play_page)
    ViewPager vpPlay;
    @Bind(R.id.il_indicator)
    IndicatorLayout ilIndicator;
    @Bind(R.id.seek_bar)
    SeekBar seekBar;
    @Bind(R.id.tv_current_time)
    TextView tvCurrentTime;
    @Bind(R.id.tv_total_time)
    TextView tvTotalTime;
    @Bind(R.id.iv_mode)
    ImageView ivMode;
    @Bind(R.id.iv_prev)
    ImageView ivPrev;
    @Bind(R.id.iv_play)
    ImageView ivPlay;
    @Bind(R.id.iv_next)
    ImageView ivNext;
    private AlbumCoverView mAlbumCoverView;
    private LrcView mLrcViewSingle;
    private LrcView mLrcViewFull;
    private List<View> mViewPagerContent;
    private int mLastProgress;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_play, container, false);
    }

    @Override
    protected void init() {
        initSystemBar();
        initViewPager();
        ilIndicator.create(mViewPagerContent.size());
        onChange(getPlayService().getPlayingMusic());
        initMode();
    }

    @Override
    protected void setListener() {
        ivBack.setOnClickListener(this);
        ivMode.setOnClickListener(this);
        ivPlay.setOnClickListener(this);
        ivPrev.setOnClickListener(this);
        ivNext.setOnClickListener(this);
        seekBar.setOnSeekBarChangeListener(this);
        vpPlay.setOnPageChangeListener(this);
    }

    /**
     * 沉浸式状态栏
     */
    private void initSystemBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int top = Utils.getSystemBarHeight(getActivity());
            llContent.setPadding(0, top, 0, 0);
        }
    }

    private void initViewPager() {
        View coverView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_play_page_cover, null);
        View lrcView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_play_page_lrc, null);
        mAlbumCoverView = (AlbumCoverView) coverView.findViewById(R.id.album_cover_view);
        mLrcViewSingle = (LrcView) coverView.findViewById(R.id.lrc_view_single);
        mLrcViewFull = (LrcView) lrcView.findViewById(R.id.lrc_view_full);
        mViewPagerContent = new ArrayList<>(2);
        mViewPagerContent.add(coverView);
        mViewPagerContent.add(lrcView);
        PlayPagerAdapter adapter = new PlayPagerAdapter(mViewPagerContent);
        vpPlay.setAdapter(adapter);
    }

    /**
     * 更新播放进度
     */
    public void onPublish(int progress) {
        seekBar.setProgress(progress);
        if (mLrcViewSingle.hasLrc()) {
            mLrcViewSingle.updateTime(progress);
            mLrcViewFull.updateTime(progress);
        }
        //更新当前播放时间
        if (progress - mLastProgress >= 1000) {
            tvCurrentTime.setText(formatTime(progress));
            mLastProgress = progress;
        }
    }

    public void onChange(Music music) {
        onPlay(music);
    }

    public void onPlayerPause() {
        ivPlay.setImageResource(R.drawable.ic_play_btn_play_selector);
        mAlbumCoverView.pause();
    }

    public void onPlayerResume() {
        ivPlay.setImageResource(R.drawable.ic_play_btn_pause_selector);
        mAlbumCoverView.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                getActivity().onBackPressed();
                break;
            case R.id.iv_mode:
                switchMode();
                break;
            case R.id.iv_play:
                play();
                break;
            case R.id.iv_prev:
                prev();
                break;
            case R.id.iv_next:
                next();
                break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        ilIndicator.setCurrent(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (getPlayService().isPlaying() || getPlayService().isPause()) {
            int progress = seekBar.getProgress();
            getPlayService().seekTo(progress);
            mLrcViewSingle.onDrag(progress);
            mLrcViewFull.onDrag(progress);
            tvCurrentTime.setText(formatTime(progress));
            mLastProgress = progress;
        } else {
            seekBar.setProgress(0);
        }
    }

    private void onPlay(Music music) {
        if (music == null) {
            return;
        }
        tvTitle.setText(music.getTitle());
        tvArtist.setText(music.getArtist());
        seekBar.setMax((int) music.getDuration());
        seekBar.setProgress(0);
        mLastProgress = 0;
        tvCurrentTime.setText(R.string.play_time_start);
        tvTotalTime.setText(formatTime(music.getDuration()));
        setCoverAndBg(music);
        setLrc(music);
        if (getPlayService().isPlaying()) {
            ivPlay.setImageResource(R.drawable.ic_play_btn_pause_selector);
            mAlbumCoverView.start();
        } else {
            ivPlay.setImageResource(R.drawable.ic_play_btn_play_selector);
            mAlbumCoverView.pause();
        }
    }

    private void play() {
        if (getPlayService().isPlaying()) {//正在播放
            getPlayService().pause();
        } else {
            if (getPlayService().isPause()) {//暂停
                getPlayService().resume();
            } else {//还未开始播放
                getPlayService().play(getPlayService().getPlayingPosition());
            }
        }
    }

    private void prev() {
        getPlayService().prev();
    }

    private void next() {
        getPlayService().next();
    }

    private void initMode() {
        PlayModeEnum mode = PlayModeEnum.valueOf((Integer) Preferences.get(getContext(), Preferences.PLAY_MODE, 1));
        switch (mode) {
            case LOOP:
                ivMode.setImageResource(R.drawable.ic_play_btn_loop_selector);
                break;
            case SHUFFLE:
                ivMode.setImageResource(R.drawable.ic_play_btn_shuffle_selector);
                break;
            case ONE:
                ivMode.setImageResource(R.drawable.ic_play_btn_one_selector);
                break;
        }
    }

    private void switchMode() {
        PlayModeEnum mode = PlayModeEnum.valueOf((Integer) Preferences.get(getContext(), Preferences.PLAY_MODE, 1));
        switch (mode) {
            case LOOP:
                mode = PlayModeEnum.SHUFFLE;
                ToastUtils.show(R.string.mode_shuffle);
                break;
            case SHUFFLE:
                mode = PlayModeEnum.ONE;
                ToastUtils.show(R.string.mode_one);
                break;
            case ONE:
                mode = PlayModeEnum.LOOP;
                ToastUtils.show(R.string.mode_loop);
                break;
        }
        Preferences.put(getContext(), Preferences.PLAY_MODE, mode.value());
        initMode();
    }

    private void setCoverAndBg(Music music) {
        if (music.getType() == MusicTypeEnum.LOACL) {
            mAlbumCoverView.setCoverBitmap(CoverLoader.getInstance().loadRound(music.getCoverUri()));
            ivPlayingBg.setImageBitmap(CoverLoader.getInstance().loadBlur(music.getCoverUri()));
        } else {
            if (music.getCover() == null) {
                mAlbumCoverView.setCoverBitmap(CoverLoader.getInstance().loadRound(null));
                ivPlayingBg.setImageResource(R.drawable.ic_play_page_default_bg);
            } else {
                Bitmap cover = ImageUtils.resizeImage(music.getCover(), Utils.getScreenWidth() / 2, Utils.getScreenWidth() / 2);
                cover = ImageUtils.createCircleImage(cover);
                mAlbumCoverView.setCoverBitmap(cover);
                Bitmap bg = ImageUtils.boxBlurFilter(music.getCover());
                ivPlayingBg.setImageBitmap(bg);
            }
        }
    }

    private void setLrc(Music music) {
        String lrcPath;
        if (music.getType() == MusicTypeEnum.LOACL) {
            lrcPath = FileUtils.getLrcDir() + music.getFileName().replace(Constants.FILENAME_MP3, Constants.FILENAME_LRC);
        } else {
            lrcPath = FileUtils.getLrcDir() + FileUtils.getLrcFileName(music.getArtist(), music.getTitle());
        }
        mLrcViewSingle.loadLrc(lrcPath);
        mLrcViewFull.loadLrc(lrcPath);
    }

    private String formatTime(long lTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
        Date date = new Date(lTime);
        return sdf.format(date);
    }
}
