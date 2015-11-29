package me.wcy.ponymusic.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import butterknife.Bind;
import me.wcy.ponymusic.R;
import me.wcy.ponymusic.model.MusicInfo;
import me.wcy.ponymusic.utils.CoverLoader;
import me.wcy.ponymusic.utils.MusicUtils;

/**
 * 正在播放界面
 * Created by wcy on 2015/11/27.
 */
public class PlayingFragment extends BaseFragment implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    @Bind(R.id.iv_playing_bg)
    ImageView ivPlayingBg;
    @Bind(R.id.iv_back)
    ImageView ivBack;
    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.tv_artist)
    TextView tvArtist;
    @Bind(R.id.seekbar)
    SeekBar seekBar;
    @Bind(R.id.iv_prev)
    ImageView ivPrev;
    @Bind(R.id.iv_play)
    ImageView ivPlay;
    @Bind(R.id.iv_next)
    ImageView ivNext;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_playing, container, false);
    }

    @Override
    protected void init() {
        onChange(mActivity.getPlayService().getPlayingPosition());
    }

    @Override
    protected void setListener() {
        ivBack.setOnClickListener(this);
        ivPlay.setOnClickListener(this);
        ivPrev.setOnClickListener(this);
        ivNext.setOnClickListener(this);
        seekBar.setOnSeekBarChangeListener(this);
    }

    /**
     * execute in worker thread
     *
     * @param progress 播放进度
     */
    public void onPublish(int progress) {
        seekBar.setProgress(progress);
    }

    public void onChange(int position) {
        onPlay(position);
        setBackground(position);
        seekBar.setProgress(0);
    }

    public void onPlayerPause() {
        ivPlay.setImageResource(R.drawable.ic_play_btn_play_selector);
    }

    public void onPlayerResume() {
        ivPlay.setImageResource(R.drawable.ic_play_btn_pause_selector);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                mActivity.onBackPressed();
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
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        int progress = seekBar.getProgress();
        mActivity.getPlayService().seekTo(progress);
    }

    private void onPlay(int position) {
        MusicInfo musicInfo = MusicUtils.sMusicList.get(position);
        tvTitle.setText(musicInfo.getTitle());
        tvArtist.setText(musicInfo.getArtist());
        seekBar.setMax((int) musicInfo.getDuration());
        if (mActivity.getPlayService().isPlaying()) {
            ivPlay.setImageResource(R.drawable.ic_play_btn_pause_selector);
        } else {
            ivPlay.setImageResource(R.drawable.ic_play_btn_play_selector);
        }
    }

    private void play() {
        if (mActivity.getPlayService().isPlaying()) {//正在播放
            mActivity.getPlayService().pause();
        } else {
            if (mActivity.getPlayService().isPause()) {//暂停
                mActivity.getPlayService().resume();
            } else {//还未开始播放
                mActivity.getPlayService().play(mActivity.getPlayService().getPlayingPosition());
            }
        }
    }

    private void prev() {
        mActivity.getPlayService().prev();
    }

    private void next() {
        mActivity.getPlayService().next();
    }

    private void setBackground(int position) {
        MusicInfo musicInfo = MusicUtils.sMusicList.get(position);
        Bitmap bmp = CoverLoader.getInstance().load(musicInfo.getCoverUri());
        if (bmp == null) {
            ivPlayingBg.setImageResource(R.drawable.ic_playing_default_bg);
        } else {
            ivPlayingBg.setImageBitmap(bmp);
        }
    }
}
