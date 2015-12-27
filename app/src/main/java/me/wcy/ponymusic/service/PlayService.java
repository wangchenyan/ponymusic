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
import java.util.Random;

import me.wcy.ponymusic.R;
import me.wcy.ponymusic.activity.MusicActivity;
import me.wcy.ponymusic.activity.SplashActivity;
import me.wcy.ponymusic.model.Music;
import me.wcy.ponymusic.utils.CoverLoader;
import me.wcy.ponymusic.enums.MusicTypeEnum;
import me.wcy.ponymusic.utils.MusicUtils;
import me.wcy.ponymusic.enums.PlayModeEnum;
import me.wcy.ponymusic.utils.Preferences;

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
    private Music mPlayingMusic;
    private int mPlayingPosition;
    private boolean mIsPause = false;

    @Override
    public void onCreate() {
        super.onCreate();
        MusicUtils.scanMusic(this);
        mPlayingPosition = (Integer) Preferences.get(this, Preferences.PLAY_POSITION, 0);
        mPlayingPosition = mPlayingPosition >= MusicUtils.getMusicList().size() ? 0 : mPlayingPosition;
        mPlayingMusic = MusicUtils.getMusicList().isEmpty() ? null : MusicUtils.getMusicList().get(mPlayingPosition);
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
        mPlayingMusic = MusicUtils.getMusicList().get(mPlayingPosition);

        try {
            mPlayer.reset();
            mPlayer.setDataSource(mPlayingMusic.getUri());
            mPlayer.prepare();
            start();
            if (mListener != null) {
                mListener.onChange(mPlayingMusic);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        updateNotification(mPlayingMusic);

        Preferences.put(this, Preferences.PLAY_POSITION, mPlayingPosition);
        return mPlayingPosition;
    }

    public void play(Music music) {
        mPlayingMusic = music;
        try {
            mPlayer.reset();
            mPlayer.setDataSource(mPlayingMusic.getUri());
            mPlayer.prepare();
            start();
            if (mListener != null) {
                mListener.onChange(mPlayingMusic);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        updateNotification(mPlayingMusic);
    }

    private void start() {
        mPlayer.start();
        updateNotification(mPlayingMusic);
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
        PlayModeEnum mode = PlayModeEnum.valueOf((Integer) Preferences.get(this, Preferences.PLAY_MODE, 1));
        switch (mode) {
            case LOOP:
                return play(mPlayingPosition + 1);
            case SHUFFLE:
                mPlayingPosition = new Random().nextInt(MusicUtils.getMusicList().size());
                return play(mPlayingPosition);
            case ONE:
                return play(mPlayingPosition);
            default:
                return play(mPlayingPosition + 1);
        }
    }

    public int prev() {
        PlayModeEnum mode = PlayModeEnum.valueOf((Integer) Preferences.get(this, Preferences.PLAY_MODE, 1));
        switch (mode) {
            case LOOP:
                return play(mPlayingPosition - 1);
            case SHUFFLE:
                mPlayingPosition = new Random().nextInt(MusicUtils.getMusicList().size());
                return play(mPlayingPosition);
            case ONE:
                return play(mPlayingPosition);
            default:
                return play(mPlayingPosition - 1);
        }
    }

    /**
     * 跳转到指定的时间位置
     *
     * @param msec 时间
     */
    public void seekTo(int msec) {
        if (isPlaying() || isPause()) {
            mPlayer.seekTo(msec);
            if (mListener != null) {
                mListener.onPublish(msec);
            }
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

    public Music getPlayingMusic() {
        return mPlayingMusic;
    }

    public void updatePlayingPosition(Music playingMusic) {
        int position = 0;
        for (int i = 0; i < MusicUtils.getMusicList().size(); i++) {
            if (MusicUtils.getMusicList().get(i).equals(playingMusic)) {
                position = i;
                break;
            }
        }
        mPlayingPosition = position;
        Preferences.put(this, Preferences.PLAY_POSITION, mPlayingPosition);
    }

    /**
     * 更新通知栏
     */
    private void updateNotification(Music music) {
        String title = music.getTitle();
        String subtitle = music.getArtist() + " - " + music.getAlbum();
        Bitmap bitmap;
        if (music.getType() == MusicTypeEnum.LOACL) {
            bitmap = CoverLoader.getInstance().loadThumbnail(music.getCoverUri());
        } else {
            bitmap = music.getCover();
        }
        Intent intent = new Intent(this, SplashActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        Notification.Builder builder = new Notification.Builder(this)
                .setContentIntent(pendingIntent)
                .setContentTitle(title)
                .setContentText(subtitle)
                .setSmallIcon(R.drawable.icon_notification)
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
        if (MusicActivity.class.getName().equals(intent.getStringExtra(MusicActivity.class.getName()))) {
            mListener = null;
        }
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
}
