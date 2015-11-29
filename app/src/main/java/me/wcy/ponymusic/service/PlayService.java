package me.wcy.ponymusic.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;

import java.io.IOException;

import me.wcy.ponymusic.utils.Constants;
import me.wcy.ponymusic.utils.MusicUtils;
import me.wcy.ponymusic.utils.SpUtils;

/**
 * Created by wcy on 2015/11/27.
 */
public class PlayService extends Service implements MediaPlayer.OnCompletionListener {
    private MediaPlayer mPlayer;
    private OnPlayerEventListener mListener;
    private PublishProgressThread mThread;
    private int mPlayingPosition;
    private boolean mIsPause = false;

    @Override
    public void onCreate() {
        super.onCreate();
        MusicUtils.scanMusic(this);
        mPlayingPosition = (Integer) SpUtils.get(this, Constants.PLAY_POSITION, 0);
        mPlayer = new MediaPlayer();
        mPlayer.setOnCompletionListener(this);
        mThread = new PublishProgressThread();
        mThread.start();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new PlayBinder();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        next();
    }

    public void setOnPlayEventListener(OnPlayerEventListener l) {
        mListener = l;
    }

    public int play(int position) {
        if (position < 0 || position >= MusicUtils.sMusicList.size()) {
            position = 0;
        }
        try {
            mPlayer.reset();
            mPlayer.setDataSource(MusicUtils.sMusicList.get(position).getUri());
            mPlayer.prepare();
            start();
            if (mListener != null) {
                mListener.onChange(position);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        mPlayingPosition = position;
        SpUtils.put(this, Constants.PLAY_POSITION, mPlayingPosition);
        return mPlayingPosition;
    }

    private void start() {
        mPlayer.start();
        mIsPause = false;
    }

    public int pause() {
        if (!isPlaying()) {
            return -1;
        }
        mPlayer.pause();
        mIsPause = true;
        if (mListener != null) {
            mListener.onPlayerPause();
        }
        return mPlayingPosition;
    }

    public int resume() {
        if (isPlaying()) {
            return -1;
        }
        start();
        if (mListener != null) {
            mListener.onPlayerResume();
        }
        return mPlayingPosition;
    }

    public int next() {
        if (mPlayingPosition + 1 >= MusicUtils.sMusicList.size()) {
            return play(0);
        }
        return play(mPlayingPosition + 1);
    }

    public int prev() {
        if (mPlayingPosition - 1 < 0) {
            return play(MusicUtils.sMusicList.size() - 1);
        }
        return play(mPlayingPosition - 1);
    }

    /**
     * 跳转到指定的时间位置
     *
     * @param msec 时间
     */
    public void seekTo(int msec) {
        int progress;
        if (isPlaying() || isPause()) {
            mPlayer.seekTo(msec);
            progress = msec;
        } else {
            progress = 0;
        }
        if (mListener != null) {
            mListener.onPublish(progress);
        }
    }

    public boolean isPlaying() {
        return mPlayer != null && mPlayer.isPlaying();
    }

    public boolean isPause() {
        return mPlayer != null && mIsPause;
    }

    public int getPlayingPosition() {
        return mPlayingPosition;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mListener = null;
        return true;
    }

    public class PlayBinder extends Binder {
        public PlayService getService() {
            return PlayService.this;
        }
    }

    public class PublishProgressThread extends Thread {
        @Override
        public void run() {
            while (true) {
                if (isPlaying() && mListener != null) {
                    mListener.onPublish(mPlayer.getCurrentPosition());
                }
                SystemClock.sleep(1000);
            }
        }
    }

    public interface OnPlayerEventListener {
        void onPublish(int progress);

        void onChange(int position);

        void onPlayerPause();

        void onPlayerResume();
    }
}
