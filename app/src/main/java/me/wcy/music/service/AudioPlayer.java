package me.wcy.music.service;

import android.content.Context;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import me.wcy.music.application.Notifier;
import me.wcy.music.enums.PlayModeEnum;
import me.wcy.music.model.Music;
import me.wcy.music.receiver.NoisyAudioStreamReceiver;
import me.wcy.music.utils.Preferences;

/**
 * Created by hzwangchenyan on 2018/1/26.
 */
public class AudioPlayer implements MediaPlayer.OnCompletionListener {
    private static final int STATE_IDLE = 0;
    private static final int STATE_PREPARING = 1;
    private static final int STATE_PLAYING = 2;
    private static final int STATE_PAUSE = 3;

    private static final long TIME_UPDATE = 300L;

    private final NoisyAudioStreamReceiver mNoisyReceiver = new NoisyAudioStreamReceiver();
    private final IntentFilter mNoisyFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);

    private Context context;
    private AudioFocusManager audioFocusManager;
    private MediaPlayer mediaPlayer;
    private Handler handler;
    private List<Music> musicList;
    private OnPlayerEventListener listener;
    private int position = 0;
    private int state = STATE_IDLE;

    public static AudioPlayer get() {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private static AudioPlayer instance = new AudioPlayer();
    }

    private AudioPlayer() {
    }

    public void init(Context context) {
        this.context = context.getApplicationContext();
        this.musicList = new ArrayList<>();
        audioFocusManager = new AudioFocusManager(context);
        mediaPlayer = new MediaPlayer();
        handler = new Handler(Looper.getMainLooper());
        mediaPlayer.setOnCompletionListener(this);
    }

    public void setOnPlayEventListener(OnPlayerEventListener listener) {
        this.listener = listener;
    }

    public void play(List<Music> musicList, int position) {
        this.musicList.clear();
        this.musicList.addAll(musicList);
        play(position);
    }

    public void addAndPlay(Music music) {
        int position = musicList.indexOf(music);
        if (position < 0) {
            musicList.add(music);
            position = musicList.size() - 1;
        }
        play(position);
    }

    public void play(int position) {
        if (musicList.isEmpty()) {
            return;
        }

        if (position < 0) {
            position = musicList.size() - 1;
        } else if (position >= musicList.size()) {
            position = 0;
        }

        this.position = position;
        Music music = musicList.get(this.position);
        Preferences.saveCurrentSongId(music.getId());

        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(music.getPath());
            mediaPlayer.prepareAsync();
            state = STATE_PREPARING;
            mediaPlayer.setOnPreparedListener(mPreparedListener);
            mediaPlayer.setOnBufferingUpdateListener(mBufferingUpdateListener);
            if (listener != null) {
                listener.onChange(music);
            }
            Notifier.get().showPlay(music);
            MediaSessionManager.get().updateMetaData(music);
            MediaSessionManager.get().updatePlaybackState();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private MediaPlayer.OnPreparedListener mPreparedListener = mp -> {
        if (isPreparing()) {
            startPlayer();
        }
    };

    private MediaPlayer.OnBufferingUpdateListener mBufferingUpdateListener = new MediaPlayer.OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(MediaPlayer mp, int percent) {
            if (listener != null) {
                listener.onBufferingUpdate(percent);
            }
        }
    };

    public void playPause() {
        if (isPreparing()) {
            stopPlayer();
        } else if (isPlaying()) {
            pausePlayer();
        } else if (isPausing()) {
            startPlayer();
        } else {
            play(position);
        }
    }

    public void startPlayer() {
        if (!isPreparing() && !isPausing()) {
            return;
        }

        if (audioFocusManager.requestAudioFocus()) {
            mediaPlayer.start();
            state = STATE_PLAYING;
            handler.post(mPublishRunnable);
            Notifier.get().showPlay(musicList.get(position));
            MediaSessionManager.get().updatePlaybackState();
            context.registerReceiver(mNoisyReceiver, mNoisyFilter);

            if (listener != null) {
                listener.onPlayerStart();
            }
        }
    }

    public void pausePlayer() {
        if (!isPlaying()) {
            return;
        }

        mediaPlayer.pause();
        state = STATE_PAUSE;
        handler.removeCallbacks(mPublishRunnable);
        Notifier.get().showPause(musicList.get(position));
        MediaSessionManager.get().updatePlaybackState();
        context.unregisterReceiver(mNoisyReceiver);
        audioFocusManager.abandonAudioFocus();

        if (listener != null) {
            listener.onPlayerPause();
        }
    }

    public void stopPlayer() {
        if (isIdle()) {
            return;
        }

        pausePlayer();
        mediaPlayer.reset();
        state = STATE_IDLE;
    }

    public void next() {
        if (musicList.isEmpty()) {
            return;
        }

        PlayModeEnum mode = PlayModeEnum.valueOf(Preferences.getPlayMode());
        switch (mode) {
            case SHUFFLE:
                position = new Random().nextInt(musicList.size());
                play(position);
                break;
            case SINGLE:
                play(position);
                break;
            case LOOP:
            default:
                play(position + 1);
                break;
        }
    }

    public void prev() {
        if (musicList.isEmpty()) {
            return;
        }

        PlayModeEnum mode = PlayModeEnum.valueOf(Preferences.getPlayMode());
        switch (mode) {
            case SHUFFLE:
                position = new Random().nextInt(musicList.size());
                play(position);
                break;
            case SINGLE:
                play(position);
                break;
            case LOOP:
            default:
                play(position - 1);
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
            mediaPlayer.seekTo(msec);
            MediaSessionManager.get().updatePlaybackState();
            if (listener != null) {
                listener.onPublish(msec);
            }
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        next();
    }

    private Runnable mPublishRunnable = new Runnable() {
        @Override
        public void run() {
            if (isPlaying() && listener != null) {
                listener.onPublish(mediaPlayer.getCurrentPosition());
            }
            handler.postDelayed(this, TIME_UPDATE);
        }
    };

    public int getAudioSessionId() {
        return mediaPlayer.getAudioSessionId();
    }

    public long getAudioPosition() {
        if (isPlaying() || isPausing()) {
            return mediaPlayer.getCurrentPosition();
        } else {
            return 0;
        }
    }

    public int getPlayPosition() {
        return position;
    }

    public Music getPlayingMusic() {
        if (musicList.isEmpty()) {
            return null;
        }
        if (position < 0 || position >= musicList.size()) {
            position = 0;
        }
        return musicList.get(position);
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public List<Music> getMusicList() {
        return musicList;
    }

    public boolean isPlaying() {
        return state == STATE_PLAYING;
    }

    public boolean isPausing() {
        return state == STATE_PAUSE;
    }

    public boolean isPreparing() {
        return state == STATE_PREPARING;
    }

    public boolean isIdle() {
        return state == STATE_IDLE;
    }
}
