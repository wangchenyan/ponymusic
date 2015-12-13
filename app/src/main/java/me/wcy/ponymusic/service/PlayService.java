package me.wcy.ponymusic.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;

import java.io.IOException;

import me.wcy.ponymusic.R;
import me.wcy.ponymusic.activity.SplashActivity;
import me.wcy.ponymusic.model.MusicInfo;
import me.wcy.ponymusic.utils.Constants;
import me.wcy.ponymusic.utils.CoverLoader;
import me.wcy.ponymusic.utils.MusicUtils;
import me.wcy.ponymusic.utils.SpUtils;

/**
 * 音乐播放后台服务
 * Created by wcy on 2015/11/27.
 */
public class PlayService extends Service implements MediaPlayer.OnCompletionListener {
    private static final int NOTIFICATION_ID = 0x111;
    private static final int MSG_UPDATE = 0;
    private static final long TIME_UPDATE = 100L;
    private MediaPlayer mPlayer;
    private OnPlayerEventListener mListener;
    private Handler mHandler;
    private int mPlayingPosition;
    private boolean mIsPause = false;

    @Override
    public void onCreate() {
        super.onCreate();
        MusicUtils.scanMusic(this);
        mPlayingPosition = (Integer) SpUtils.get(this, Constants.PLAY_POSITION, 0);
        if (mPlayingPosition >= MusicUtils.getMusicList().size()) {
            mPlayingPosition = 0;
        }
        mPlayer = new MediaPlayer();
        mPlayer.setOnCompletionListener(this);
        mHandler = new PublishProgressHandler();
        mHandler.sendEmptyMessageDelayed(MSG_UPDATE, TIME_UPDATE);
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

    public void setOnPlayEventListener(OnPlayerEventListener listener) {
        mListener = listener;
    }

    public int play(int position) {
        if (MusicUtils.getMusicList().isEmpty()) {
            return -1;
        }

        if (position < 0) {
            position = MusicUtils.getMusicList().size() - 1;
        } else if (position >= MusicUtils.getMusicList().size()) {
            position = 0;
        }

        mPlayingPosition = position;

        try {
            mPlayer.reset();
            mPlayer.setDataSource(MusicUtils.getMusicList().get(mPlayingPosition).getUri());
            mPlayer.prepare();
            start();
            if (mListener != null) {
                mListener.onChange(mPlayingPosition);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        updateNotification(mPlayingPosition);

        SpUtils.put(this, Constants.PLAY_POSITION, mPlayingPosition);
        return mPlayingPosition;
    }

    private void start() {
        mPlayer.start();
        updateNotification(mPlayingPosition);
        mIsPause = false;
    }

    public int pause() {
        if (!isPlaying()) {
            return -1;
        }
        mPlayer.pause();
        clearNotification();
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
        return play(mPlayingPosition + 1);
    }

    public int prev() {
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

    public void setPlayingPosition(int position) {
        mPlayingPosition = position;
        SpUtils.put(this, Constants.PLAY_POSITION, mPlayingPosition);
    }

    /**
     * 更新通知栏
     */
    private void updateNotification(int position) {
        MusicInfo musicInfo = MusicUtils.getMusicList().get(position);
        String title = musicInfo.getTitle();
        String subtitle = musicInfo.getArtist() + " - " + musicInfo.getAlbum();
        Bitmap bitmap = CoverLoader.getInstance().loadThumbnail(musicInfo.getCoverUri());
        Intent intent = new Intent(this, SplashActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        Notification.Builder builder = new Notification.Builder(this)
                .setContentIntent(pendingIntent)
                .setContentTitle(title)
                .setContentText(subtitle)
                .setSmallIcon(R.drawable.icon)
                .setLargeIcon(bitmap)
                .setOngoing(true);
        Notification notification = builder.getNotification();
        startForeground(NOTIFICATION_ID, notification);
    }

    /**
     * 清除通知栏
     */
    private void clearNotification() {
        stopForeground(true);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mListener = null;
        return true;
    }

    @Override
    public void onDestroy() {
        pause();
        super.onDestroy();
    }

    public class PlayBinder extends Binder {
        public PlayService getService() {
            return PlayService.this;
        }
    }

    private class PublishProgressHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_UPDATE) {
                if (isPlaying() && mListener != null) {
                    mListener.onPublish(mPlayer.getCurrentPosition());
                }
                mHandler.sendEmptyMessageDelayed(MSG_UPDATE, TIME_UPDATE);
            }
        }
    }

    public interface OnPlayerEventListener {
        /**
         * 更新进度
         */
        void onPublish(int progress);

        /**
         * 切换歌曲
         */
        void onChange(int position);

        /**
         * 暂停播放
         */
        void onPlayerPause();

        /**
         * 继续播放
         */
        void onPlayerResume();
    }
}
