package me.wcy.music.activity;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.media.audiofx.AudioEffect;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.text.TextUtils;

import me.wcy.music.R;
import me.wcy.music.service.EventCallback;
import me.wcy.music.service.OnPlayerEventListener;
import me.wcy.music.service.PlayService;
import me.wcy.music.utils.MusicUtils;
import me.wcy.music.utils.Preferences;
import me.wcy.music.utils.ToastUtils;

public class SettingActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        if (!checkServiceAlive()) {
            return;
        }

        SettingFragment settingFragment = new SettingFragment();
        settingFragment.setPlayService(getPlayService());
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.ll_fragment_container, settingFragment)
                .commit();
    }

    public static class SettingFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener {
        private Preference mSoundEffect;
        private Preference mFilterSize;
        private Preference mFilterTime;

        private PlayService mPlayService;
        private ProgressDialog mProgressDialog;

        public void setPlayService(PlayService playService) {
            this.mPlayService = playService;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preference_setting);

            mSoundEffect = findPreference(getString(R.string.setting_key_sound_effect));
            mFilterSize = findPreference(getString(R.string.setting_key_filter_size));
            mFilterTime = findPreference(getString(R.string.setting_key_filter_time));
            mSoundEffect.setOnPreferenceClickListener(this);
            mFilterSize.setOnPreferenceChangeListener(this);
            mFilterTime.setOnPreferenceChangeListener(this);

            mFilterSize.setSummary(getSummary(Preferences.getFilterSize(), R.array.filter_size_entries, R.array.filter_size_entry_values));
            mFilterTime.setSummary(getSummary(Preferences.getFilterTime(), R.array.filter_time_entries, R.array.filter_time_entry_values));
        }

        @Override
        public boolean onPreferenceClick(Preference preference) {
            if (preference == mSoundEffect) {
                startEqualizer();
                return true;
            }
            return false;
        }

        private void startEqualizer() {
            if (MusicUtils.isAudioControlPanelAvailable(getActivity())) {
                Intent intent = new Intent();
                String packageName = getActivity().getPackageName();
                intent.setAction(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL);
                intent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, packageName);
                intent.putExtra(AudioEffect.EXTRA_CONTENT_TYPE, AudioEffect.CONTENT_TYPE_MUSIC);
                intent.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, mPlayService.getAudioSessionId());

                try {
                    startActivityForResult(intent, 1);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                    ToastUtils.show(R.string.device_not_support);
                }
            } else {
                ToastUtils.show(R.string.device_not_support);
            }
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            if (preference == mFilterSize) {
                Preferences.saveFilterSize((String) newValue);
                mFilterSize.setSummary(getSummary(Preferences.getFilterSize(), R.array.filter_size_entries, R.array.filter_size_entry_values));
                onFilterChanged();
                return true;
            } else if (preference == mFilterTime) {
                Preferences.saveFilterTime((String) newValue);
                mFilterTime.setSummary(getSummary(Preferences.getFilterTime(), R.array.filter_time_entries, R.array.filter_time_entry_values));
                onFilterChanged();
                return true;
            }
            return false;
        }

        private String getSummary(String value, int entries, int entryValues) {
            String[] entryArray = getResources().getStringArray(entries);
            String[] entryValueArray = getResources().getStringArray(entryValues);
            for (int i = 0; i < entryValueArray.length; i++) {
                String v = entryValueArray[i];
                if (TextUtils.equals(v, value)) {
                    return entryArray[i];
                }
            }
            return entryArray[0];
        }

        private void onFilterChanged() {
            showProgress();
            mPlayService.stop();
            mPlayService.updateMusicList(new EventCallback<Void>() {
                @Override
                public void onEvent(Void aVoid) {
                    cancelProgress();
                    OnPlayerEventListener listener = mPlayService.getOnPlayEventListener();
                    if (listener != null) {
                        listener.onChange(mPlayService.getPlayingMusic());
                    }
                }
            });
        }

        private void showProgress() {
            if (mProgressDialog == null) {
                mProgressDialog = new ProgressDialog(getActivity());
                mProgressDialog.setMessage("正在扫描音乐");
            }
            if (!mProgressDialog.isShowing()) {
                mProgressDialog.show();
            }
        }

        private void cancelProgress() {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.cancel();
            }
        }
    }
}
