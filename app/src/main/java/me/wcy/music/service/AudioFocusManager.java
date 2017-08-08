package me.wcy.music.service;

import android.media.AudioManager;
import android.support.annotation.NonNull;

import static android.content.Context.AUDIO_SERVICE;

/**
 * Created by hzwangchenyan on 2017/8/8.
 */
public class AudioFocusManager implements AudioManager.OnAudioFocusChangeListener {
    private PlayService mPlayService;
    private AudioManager mAudioManager;
    private boolean isPausedByFocusLossTransient;
    private int mVolumeWhenFocusLossTransientCanDuck;

    public AudioFocusManager(@NonNull PlayService playService) {
        mPlayService = playService;
        mAudioManager = (AudioManager) playService.getSystemService(AUDIO_SERVICE);
    }

    public boolean requestAudioFocus() {
        return mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)
                == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
    }

    public void abandonAudioFocus() {
        mAudioManager.abandonAudioFocus(this);
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        int volume;
        switch (focusChange) {
            // 重新获得焦点
            case AudioManager.AUDIOFOCUS_GAIN:
                if (!willPlay() && isPausedByFocusLossTransient) {
                    // 通话结束，恢复播放
                    mPlayService.playPause();
                }

                volume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                if (mVolumeWhenFocusLossTransientCanDuck > 0 && volume == mVolumeWhenFocusLossTransientCanDuck / 2) {
                    // 恢复音量
                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mVolumeWhenFocusLossTransientCanDuck,
                            AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
                }

                isPausedByFocusLossTransient = false;
                mVolumeWhenFocusLossTransientCanDuck = 0;
                break;
            // 永久丢失焦点，如被其他播放器抢占
            case AudioManager.AUDIOFOCUS_LOSS:
                if (willPlay()) {
                    forceStop();
                }
                break;
            // 短暂丢失焦点，如来电
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                if (willPlay()) {
                    forceStop();
                    isPausedByFocusLossTransient = true;
                }
                break;
            // 瞬间丢失焦点，如通知
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                // 音量减小为一半
                volume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                if (willPlay() && volume > 0) {
                    mVolumeWhenFocusLossTransientCanDuck = volume;
                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mVolumeWhenFocusLossTransientCanDuck / 2,
                            AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
                }
                break;
        }
    }

    private boolean willPlay() {
        return mPlayService.isPreparing() || mPlayService.isPlaying();
    }

    private void forceStop() {
        if (mPlayService.isPreparing()) {
            mPlayService.stop();
        } else if (mPlayService.isPlaying()) {
            mPlayService.pause();
        }
    }
}
