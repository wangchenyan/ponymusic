package me.wcy.ponymusic.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import me.wcy.ponymusic.R;
import me.wcy.ponymusic.adapter.PlayPagerAdapter;
import me.wcy.ponymusic.model.MusicInfo;
import me.wcy.ponymusic.utils.Constants;
import me.wcy.ponymusic.utils.CoverLoader;
import me.wcy.ponymusic.utils.MusicUtils;
import me.wcy.ponymusic.widget.AlbumCoverView;
import me.wcy.ponymusic.widget.IndicatorLayout;
import me.wcy.ponymusic.widget.LrcView;

/**
 * 正在播放界面
 * Created by wcy on 2015/11/27.
 */
public class PlayFragment extends BaseFragment implements View.OnClickListener, ViewPager.OnPageChangeListener, SeekBar.OnSeekBarChangeListener {
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_play, container, false);
    }

    @Override
    protected void init() {
        initViewPager();
        ilIndicator.create(mViewPagerContent.size());
        onChange(getPlayService().getPlayingPosition());
    }

    @Override
    protected void setListener() {
        ivBack.setOnClickListener(this);
        ivPlay.setOnClickListener(this);
        ivPrev.setOnClickListener(this);
        ivNext.setOnClickListener(this);
        seekBar.setOnSeekBarChangeListener(this);
        vpPlay.setOnPageChangeListener(this);
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
    }

    public void onChange(int position) {
        onPlay(position);
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
        int progress = seekBar.getProgress();
        getPlayService().seekTo(progress);
        mLrcViewSingle.onDrag(progress);
        mLrcViewFull.onDrag(progress);
    }

    private void onPlay(int position) {
        if (MusicUtils.getMusicList().isEmpty()) {
            return;
        }

        MusicInfo musicInfo = MusicUtils.getMusicList().get(position);
        tvTitle.setText(musicInfo.getTitle());
        tvArtist.setText(musicInfo.getArtist());
        seekBar.setMax((int) musicInfo.getDuration());
        seekBar.setProgress(0);
        setBackground(position);
        setAlbumCover(position);
        setLrc(position);
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

    private void setBackground(int position) {
        MusicInfo musicInfo = MusicUtils.getMusicList().get(position);
        Bitmap bitmap = CoverLoader.getInstance().loadBlur(musicInfo.getCoverUri());
        ivPlayingBg.setImageBitmap(bitmap);
    }

    private void setAlbumCover(int position) {
        MusicInfo musicInfo = MusicUtils.getMusicList().get(position);
        Bitmap bitmap = CoverLoader.getInstance().loadRound(musicInfo.getCoverUri());
        mAlbumCoverView.setCoverBitmap(bitmap);
    }

    private void setLrc(int position) {
        MusicInfo musicInfo = MusicUtils.getMusicList().get(position);
        String lrcPath = MusicUtils.getLrcDir() + musicInfo.getFileName().replace(Constants.FILENAME_MP3, Constants.FILENAME_LRC);
        mLrcViewSingle.loadLrc(lrcPath);
        mLrcViewFull.loadLrc(lrcPath);
    }
}
