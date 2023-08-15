package me.wcy.music.activity

import android.content.ActivityNotFoundException
import android.content.Intent
import android.media.audiofx.AudioEffect
import android.os.Bundle
import android.preference.Preference
import android.preference.Preference.OnPreferenceChangeListener
import android.preference.Preference.OnPreferenceClickListener
import android.preference.PreferenceFragment
import android.text.TextUtils
import com.hwangjr.rxbus.RxBus
import me.wcy.music.R
import me.wcy.music.constants.RxBusTags
import me.wcy.music.service.AudioPlayer
import me.wcy.music.storage.preference.Preferences
import me.wcy.music.utils.MusicUtils
import me.wcy.music.utils.ToastUtils

class SettingActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        val settingFragment = SettingFragment()
        fragmentManager.beginTransaction().replace(R.id.ll_fragment_container, settingFragment)
            .commit()
    }

    class SettingFragment : PreferenceFragment(), OnPreferenceClickListener,
        OnPreferenceChangeListener {
        private var mSoundEffect: Preference? = null
        private var mFilterSize: Preference? = null
        private var mFilterTime: Preference? = null
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.preference_setting)
            mSoundEffect = findPreference(getString(R.string.setting_key_sound_effect))
            mFilterSize = findPreference(getString(R.string.setting_key_filter_size))
            mFilterTime = findPreference(getString(R.string.setting_key_filter_time))
            mSoundEffect?.setOnPreferenceClickListener(this)
            mFilterSize?.setOnPreferenceChangeListener(this)
            mFilterTime?.setOnPreferenceChangeListener(this)
            mFilterSize?.setSummary(
                getSummary(
                    Preferences.filterSize, R.array.filter_size_entries,
                    R.array.filter_size_entry_values
                )
            )
            mFilterTime?.setSummary(
                getSummary(
                    Preferences.filterTime, R.array.filter_time_entries,
                    R.array.filter_time_entry_values
                )
            )
        }

        override fun onPreferenceClick(preference: Preference): Boolean {
            if (preference === mSoundEffect) {
                startEqualizer()
                return true
            }
            return false
        }

        private fun startEqualizer() {
            if (MusicUtils.isAudioControlPanelAvailable(activity)) {
                val intent = Intent()
                val packageName = activity.packageName
                intent.action = AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL
                intent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, packageName)
                intent.putExtra(AudioEffect.EXTRA_CONTENT_TYPE, AudioEffect.CONTENT_TYPE_MUSIC)
                intent.putExtra(
                    AudioEffect.EXTRA_AUDIO_SESSION,
                    AudioPlayer.get().audioSessionId
                )
                try {
                    startActivityForResult(intent, 1)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                    ToastUtils.show(R.string.device_not_support)
                }
            } else {
                ToastUtils.show(R.string.device_not_support)
            }
        }

        override fun onPreferenceChange(preference: Preference, newValue: Any): Boolean {
            if (preference === mFilterSize) {
                Preferences.saveFilterSize(newValue as String)
                mFilterSize!!.summary = getSummary(
                    Preferences.filterSize, R.array.filter_size_entries,
                    R.array.filter_size_entry_values
                )
                RxBus.get().post(RxBusTags.SCAN_MUSIC, 1)
                return true
            } else if (preference === mFilterTime) {
                Preferences.saveFilterTime(newValue as String)
                mFilterTime!!.summary = getSummary(
                    Preferences.filterTime, R.array.filter_time_entries,
                    R.array.filter_time_entry_values
                )
                RxBus.get().post(RxBusTags.SCAN_MUSIC, 1)
                return true
            }
            return false
        }

        private fun getSummary(value: String?, entries: Int, entryValues: Int): String {
            val entryArray = resources.getStringArray(entries)
            val entryValueArray = resources.getStringArray(entryValues)
            for (i in entryValueArray.indices) {
                val v = entryValueArray[i]
                if (TextUtils.equals(v, value)) {
                    return entryArray[i]
                }
            }
            return entryArray[0]
        }
    }
}