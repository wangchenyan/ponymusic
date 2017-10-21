package me.wcy.music.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import me.wcy.music.application.AppCache;
import me.wcy.music.application.Notifier;
import me.wcy.music.constants.Actions;
import me.wcy.music.enums.PlayModeEnum;
import me.wcy.music.model.Music;
import me.wcy.music.receiver.NoisyAudioStreamReceiver;
import me.wcy.music.utils.MusicUtils;
import me.wcy.music.utils.Preferences;

/**
 * 音乐播放后台服务
 * Created by wcy on 2015/11/27.
 */
public class PlayService extends Service implements MediaPlayer.OnCompletionListener {
    private static final String TAG = "Service";
    private static final long TIME_UPDATE = 300L;

    private static final int STATE_IDLE = 0;
    private static final int STATE_PREPARING = 1;
    private static final int STATE_PLAYING = 2;
    private static final int STATE_PAUSE = 3;

    private final NoisyAudioStreamReceiver mNoisyReceiver = new NoisyAudioStreamReceiver();
    private final IntentFilter mNoisyFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
    private final Handler mHandler = new Handler();
    private MediaPlayer mPlayer = new MediaPlayer();
    private AudioFocusManager mAudioFocusManager;
    private MediaSessionManager mMediaSessionManager;
    private OnPlayerEventListener mListener;
    // 正在播放的歌曲[本地|网络]
    private Music mPlayingMusic;
    // 正在播放的本地歌曲的序号
    private int mPlayingPosition = -1;
    private int mPlayState = STATE_IDLE;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate: " + getClass().getSimpleName());
        mAudioFocusManager = new AudioFocusManager(this);
        mMediaSessionManager = new MediaSessionManager(this);
        mPlayer.setOnCompletionListener(this);
        Notifier.init(this);
        QuitTimer.getInstance().init(this, mHandler, new EventCallback<Long>() {
            @Override
            public void onEvent(Long aLong) {
                if (mListener != null) {
                    mListener.onTimer(aLong);
                }
            }
        });
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new PlayBinder();
    }

    public static void startCommand(Context context, String action) {
        Intent intent = new Intent(context, PlayService.class);
        intent.setAction(action);
        context.startService(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null) {
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
        }
        return START_NOT_STICKY;
    }

    /**
     * 扫描音乐
     */
    public void updateMusicList(final EventCallback<Void> callback) {
        new AsyncTask<Void, Void, List<Music>>() {
            @Override
            protected List<Music> doInBackground(Void... params) {
                return MusicUtils.scanMusic(PlayService.this);
            }

            @Override
            protected void onPostExecute(List<Music> musicList) {
                AppCache.getMusicList().clear();
                AppCache.getMusicList().addAll(musicList);

                if (!AppCache.getMusicList().isEmpty()) {
                    updatePlayingPosition();
                    mPlayingMusic = AppCache.getMusicList().get(mPlayingPosition);
                }

                if (mListener != null) {
                    mListener.onMusicListUpdate();
                }

                if (callback != null) {
                    callback.onEvent(null);
                }
            }
        }.execute();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        next();
    }

    public OnPlayerEventListener getOnPlayEventListener() {
        return mListener;
    }

    public void setOnPlayEventListener(OnPlayerEventListener listener) {
        mListener = listener;
    }

    public void play(int position) {
        if (AppCache.getMusicList().isEmpty()) {
            return;
        }

        if (position < 0) {
            position = AppCache.getMusicList().size() - 1;
        } else if (position >= AppCache.getMusicList().size()) {
            position = 0;
        }

        mPlayingPosition = position;
        Music music = AppCache.getMusicList().get(mPlayingPosition);
        Preferences.saveCurrentSongId(music.getId());
        play(music);
    }

    public void play(Music music) {
        mPlayingMusic = music;
        try {
            mPlayer.reset();
            mPlayer.setDataSource(music.getPath());
            mPlayer.prepareAsync();
            mPlayState = STATE_PREPARING;
            mPlayer.setOnPreparedListener(mPreparedListener);
            mPlayer.setOnBufferingUpdateListener(mBufferingUpdateListener);
            if (mListener != null) {
                mListener.onChange(music);
            }
            Notifier.showPlay(music);
            mMediaSessionManager.updateMetaData(mPlayingMusic);
            mMediaSessionManager.updatePlaybackState();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private MediaPlayer.OnPreparedListener mPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            if (isPreparing()) {
                start();
            }
        }
    };

    private MediaPlayer.OnBufferingUpdateListener mBufferingUpdateListener = new MediaPlayer.OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(MediaPlayer mp, int percent) {
            if (mListener != null) {
                mListener.onBufferingUpdate(percent);
            }
        }
    };

    public void playPause() {
        if (isPreparing()) {
            stop();
        } else if (isPlaying()) {
            pause();
        } else if (isPausing()) {
            start();
        } else {
            play(getPlayingPosition());
        }
    }

    void start() {
        if (!isPreparing() && !isPausing()) {
            return;
        }

        if (mAudioFocusManager.requestAudioFocus()) {
            mPlayer.start();
            mPlayState = STATE_PLAYING;
            mHandler.post(mPublishRunnable);
            Notifier.showPlay(mPlayingMusic);
            mMediaSessionManager.updatePlaybackState();
            registerReceiver(mNoisyReceiver, mNoisyFilter);

            if (mListener != null) {
                mListener.onPlayerStart();
            }
        }
    }

    void pause() {
        if (!isPlaying()) {
            return;
        }

        mPlayer.pause();
        mPlayState = STATE_PAUSE;
        mHandler.removeCallbacks(mPublishRunnable);
        Notifier.showPause(mPlayingMusic);
        mMediaSessionManager.updatePlaybackState();
        unregisterReceiver(mNoisyReceiver);

        if (mListener != null) {
            mListener.onPlayerPause();
        }
    }

    public void stop() {
        if (isIdle()) {
            return;
        }

        pause();
        mPlayer.reset();
        mPlayState = STATE_IDLE;
    }

    public void next() {
        if (AppCache.getMusicList().isEmpty()) {
            return;
        }

        PlayModeEnum mode = PlayModeEnum.valueOf(Preferences.getPlayMode());
        switch (mode) {
            case SHUFFLE:
                mPlayingPosition = new Random().nextInt(AppCache.getMusicList().size());
                play(mPlayingPosition);
                break;
            case SINGLE:
                play(mPlayingPosition);
                break;
            case LOOP:
            default:
                play(mPlayingPosition + 1);
                break;
        }
    }

    public void prev() {
        if (AppCache.getMusicList().isEmpty()) {
            return;
        }

        PlayModeEnum mode = PlayModeEnum.valueOf(Preferences.getPlayMode());
        switch (mode) {
            case SHUFFLE:
                mPlayingPosition = new Random().nextInt(AppCache.getMusicList().size());
                play(mPlayingPosition);
                break;
            case SINGLE:
                play(mPlayingPosition);
                break;
            case LOOP:
            default:
                play(mPlayingPosition - 1);
                break;
        }
    }

    /**
     * 跳转到指定的时间位置
     *
     * @param msec 时间
     */
    public void seekTo(int msec) {
        if (isPlaying() || isPausing()) {
            mPlayer.seekTo(msec);
            mMediaSessionManager.updatePlaybackState();
            if (mListener != null) {
                mListener.onPublish(msec);
            }
        }
    }

    public boolean isPlaying() {
        return mPlayState == STATE_PLAYING;
    }

    public boolean isPausing() {
        return mPlayState == STATE_PAUSE;
    }

    public boolean isPreparing() {
        return mPlayState == STATE_PREPARING;
    }

    public boolean isIdle() {
        return mPlayState == STATE_IDLE;
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
        long id = Preferences.getCurrentSongId();
        for (int i = 0; i < AppCache.getMusicList().size(); i++) {
            if (AppCache.getMusicList().get(i).getId() == id) {
                position = i;
                break;
            }
        }
        mPlayingPosition = position;
        Preferences.saveCurrentSongId(AppCache.getMusicList().get(mPlayingPosition).getId());
    }

    public int getAudioSessionId() {
        return mPlayer.getAudioSessionId();
    }

    public long getCurrentPosition() {
        if (isPlaying() || isPausing()) {
            return mPlayer.getCurrentPosition();
        } else {
            return 0;
        }
    }

    private Runnable mPublishRunnable = new Runnable() {
        @Override
        public void run() {
            if (isPlaying() && mListener != null) {
                mListener.onPublish(mPlayer.getCurrentPosition());
            }
            mHandler.postDelayed(this, TIME_UPDATE);
        }
    };

    @Override
    public void onDestroy() {
        mPlayer.reset();
        mPlayer.release();
        mPlayer = null;
        mAudioFocusManager.abandonAudioFocus();
        mMediaSessionManager.release();
        Notifier.cancelAll();
        AppCache.setPlayService(null);
        super.onDestroy();
        Log.i(TAG, "onDestroy: " + getClass().getSimpleName());
    }

    public void quit() {
        stop();
        QuitTimer.getInstance().stop();
        stopSelf();
    }

    public class PlayBinder extends Binder {
        public PlayService getService() {
            return PlayService.this;
        }
    }
}
