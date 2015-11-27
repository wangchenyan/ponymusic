package me.wcy.ponymusic.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;

import me.wcy.ponymusic.utils.MusicUtils;

/**
 * Created by wcy on 2015/11/27.
 */
public class PlayService extends Service implements MediaPlayer.OnCompletionListener {
    private MediaPlayer mPlayer;
    private OnPlayEventListener mListener;
    private PublishProgressThread mThread;
    private int mPlayingPosition;

    @Override
    public void onCreate() {
        super.onCreate();
        MusicUtils.scanMusic(this);
        mPlayingPosition = 0;
        mPlayer = new MediaPlayer();
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

    }

    public void setOnPlayEventListener(OnPlayEventListener l) {
        mListener = l;
    }

    public int getPlayingPosition() {
        return mPlayingPosition;
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
                if (mPlayer != null && mListener != null && mPlayer.isPlaying()) {
                    mListener.onPublish(mPlayer.getCurrentPosition());
                }
                SystemClock.sleep(200);
            }
        }
    }

    public interface OnPlayEventListener {
        void onPublish(int percent);

        void onChange(int position);
    }
}
