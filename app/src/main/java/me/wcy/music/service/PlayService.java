package me.wcy.music.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.util.Random;

import me.wcy.music.R;
import me.wcy.music.activity.MusicActivity;
import me.wcy.music.enums.MusicTypeEnum;
import me.wcy.music.enums.PlayModeEnum;
import me.wcy.music.model.Music;
import me.wcy.music.receiver.NoisyAudioStreamReceiver;
import me.wcy.music.utils.Actions;
import me.wcy.music.utils.CoverLoader;
import me.wcy.music.utils.MusicUtils;
import me.wcy.music.utils.Preferences;
import me.wcy.music.utils.ScreenUtils;

/**
 * 音乐播放后台服务
 * Created by wcy on 2015/11/27.
 */
public class PlayService extends Service implements MediaPlayer.OnCompletionListener, AudioManager.OnAudioFocusChangeListener {
    private static final int NOTIFICATION_ID = 0x111;
    private static final long TIME_UPDATE = 100L;
    private MediaPlayer mPlayer;
    private OnPlayerEventListener mListener;
    private AudioManager mAudioManager;
    private IntentFilter mNoisyFilter;
    private NoisyAudioStreamReceiver mNoisyReceiver;
    private Handler mHandler;
    // 正在播放的歌曲[本地|网络]
    private Music mPlayingMusic;
    // 正在播放的本地歌曲的序号
    private int mPlayingPosition;
    private boolean mIsPause = false;

    @Override
    public void onCreate() {
        super.onCreate();
        mPlayer = new MediaPlayer();
        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        mNoisyFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        mNoisyReceiver = new NoisyAudioStreamReceiver();
        mHandler = new Handler();
        mPlayer.setOnCompletionListener(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new PlayBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null || intent.getAction() == null) {
            return START_STICKY_COMPATIBILITY;
        }
        switch (intent.getAction()) {
            case Actions.ACTION_MEDIA_PLAY_PAUSE:
                playPause();
                break;
            case Actions.ACTION_MEDIA_NEXT:
                next();
                break;
            case Actions.ACTION_MEDIA_PREVIOUS:
                prev();
                break;
        }
        return START_STICKY_COMPATIBILITY;
    }

    /**
     * 每次启动时扫描音乐
     */
    public void updateMusicList() {
        MusicUtils.scanMusic(this);
        if (MusicUtils.getMusicList().isEmpty()) {
            return;
        }
        updatePlayingPosition();
        mPlayingMusic = mPlayingMusic == null ? MusicUtils.getMusicList().get(mPlayingPosition) : mPlayingMusic;
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

        Preferences.saveCurrentSongId(mPlayingMusic.getId());
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
    }

    public void playPause() {
        if (isPlaying()) {
            pause();
        } else if (isPause()) {
            resume();
        } else {
            play(getPlayingPosition());
        }
    }

    private void start() {
        mPlayer.start();
        mIsPause = false;
        mHandler.post(mBackgroundRunnable);
        updateNotification(mPlayingMusic);
        mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        registerReceiver(mNoisyReceiver, mNoisyFilter);
    }

    public int pause() {
        if (!isPlaying()) {
            return -1;
        }
        mPlayer.pause();
        mIsPause = true;
        mHandler.removeCallbacks(mBackgroundRunnable);
        cancelNotification();
        mAudioManager.abandonAudioFocus(this);
        unregisterReceiver(mNoisyReceiver);
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
        PlayModeEnum mode = PlayModeEnum.valueOf(Preferences.getPlayMode(0));
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
        PlayModeEnum mode = PlayModeEnum.valueOf(Preferences.getPlayMode(0));
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

    /**
     * 获取正在播放的本地歌曲的序号
     */
    public int getPlayingPosition() {
        return mPlayingPosition;
    }

    /**
     * 获取正在播放的歌曲[本地|网络]
     */
    public Music getPlayingMusic() {
        return mPlayingMusic;
    }

    /**
     * 删除或下载歌曲后刷新正在播放的本地歌曲的序号
     */
    public void updatePlayingPosition() {
        int position = 0;
        long id = Preferences.getCurrentSongId(-1);
        for (int i = 0; i < MusicUtils.getMusicList().size(); i++) {
            if (MusicUtils.getMusicList().get(i).getId() == id) {
                position = i;
                break;
            }
        }
        mPlayingPosition = position;
        Preferences.saveCurrentSongId(MusicUtils.getMusicList().get(mPlayingPosition).getId());
    }

    /**
     * 更新通知栏
     */
    private void updateNotification(Music music) {
        String title = music.getTitle();
        String subtitle = ScreenUtils.getArtistAndAlbum(music.getArtist(), music.getAlbum());
        Bitmap bitmap;
        if (music.getType() == MusicTypeEnum.LOCAL) {
            bitmap = CoverLoader.getInstance().loadThumbnail(music.getCoverUri());
        } else {
            bitmap = music.getCover();
        }
        Intent intent = new Intent(this, MusicActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        Notification.Builder builder = new Notification.Builder(this)
                .setContentIntent(pendingIntent)
                .setContentTitle(title)
                .setContentText(subtitle)
                .setSmallIcon(R.drawable.ic_notification)
                .setLargeIcon(bitmap)
                .setOngoing(true);
        Notification notification = builder.getNotification();
        startForeground(NOTIFICATION_ID, notification);
    }

    private void cancelNotification() {
        stopForeground(true);
    }

    private Runnable mBackgroundRunnable = new Runnable() {
        @Override
        public void run() {
            if (isPlaying() && mListener != null) {
                mListener.onPublish(mPlayer.getCurrentPosition());
            }
            mHandler.postDelayed(this, TIME_UPDATE);
        }
    };

    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_LOSS:
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                if (isPlaying()) {
                    pause();
                }
                break;
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mListener = null;
        return true;
    }

    public void stop() {
        pause();
        mPlayer.reset();
        mPlayer.release();
        mPlayer = null;
        stopSelf();
    }

    public class PlayBinder extends Binder {
        public PlayService getService() {
            return PlayService.this;
        }
    }
}
