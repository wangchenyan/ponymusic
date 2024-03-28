package me.wcy.music.main

import android.content.ActivityNotFoundException
import android.content.Intent
import android.media.audiofx.AudioEffect
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import dagger.hilt.android.AndroidEntryPoint
import me.wcy.music.R
import me.wcy.music.common.BaseMusicActivity
import me.wcy.music.common.DarkModeService
import me.wcy.music.consts.PreferenceName
import me.wcy.music.service.PlayerController
import me.wcy.music.storage.preference.ConfigPreferences
import me.wcy.music.utils.MusicUtils
import me.wcy.router.annotation.Route
import top.wangchenyan.common.ext.toast
import javax.inject.Inject

@Route("/settings")
@AndroidEntryPoint
class SettingsActivity : BaseMusicActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        val fragment = SettingsFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commitAllowingStateLoss()
    }

    @AndroidEntryPoint
    class SettingsFragment : PreferenceFragmentCompat() {
        private val darkMode: Preference by lazy {
            findPreference(getString(R.string.setting_key_dark_mode))!!
        }
        private val playSoundQuality: Preference by lazy {
            findPreference(getString(R.string.setting_key_play_sound_quality))!!
        }
        private val soundEffect: Preference by lazy {
            findPreference(getString(R.string.setting_key_sound_effect))!!
        }
        private val downloadSoundQuality: Preference by lazy {
            findPreference(getString(R.string.setting_key_download_sound_quality))!!
        }
        private val filterSize: Preference by lazy {
            findPreference(getString(R.string.setting_key_filter_size))!!
        }
        private val filterTime: Preference by lazy {
            findPreference(getString(R.string.setting_key_filter_time))!!
        }

        @Inject
        lateinit var playerController: PlayerController

        @Inject
        lateinit var darkModeService: DarkModeService

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            preferenceManager.sharedPreferencesName = PreferenceName.CONFIG
            addPreferencesFromResource(R.xml.preference_setting)

            initDarkMode()
            initPlaySoundQuality()
            initSoundEffect()
            initDownloadSoundQuality()
            initFilter()
        }

        private fun initDarkMode() {
            darkMode.summary = getSummary(
                ConfigPreferences.darkMode,
                R.array.dark_mode_entries,
                R.array.dark_mode_values
            )
            darkMode.setOnPreferenceChangeListener { preference, newValue ->
                val value = newValue.toString()
                filterSize.summary = getSummary(
                    value,
                    R.array.dark_mode_entries,
                    R.array.dark_mode_values
                )
                val mode = DarkModeService.DarkMode.fromValue(value)
                darkModeService.setDarkMode(mode)
                true
            }
        }

        private fun initPlaySoundQuality() {
            playSoundQuality.summary = getSummary(
                ConfigPreferences.playSoundQuality,
                R.array.sound_quality_entries,
                R.array.sound_quality_entry_values
            )
            playSoundQuality.setOnPreferenceChangeListener { preference, newValue ->
                val value = newValue.toString()
                playSoundQuality.summary = getSummary(
                    value,
                    R.array.sound_quality_entries,
                    R.array.sound_quality_entry_values
                )
                true
            }
        }

        private fun initSoundEffect() {
            soundEffect.setOnPreferenceClickListener {
                startEqualizer()
                true
            }
        }

        private fun initDownloadSoundQuality() {
            downloadSoundQuality.summary = getSummary(
                ConfigPreferences.downloadSoundQuality,
                R.array.sound_quality_entries,
                R.array.sound_quality_entry_values
            )
            downloadSoundQuality.setOnPreferenceChangeListener { preference, newValue ->
                val value = newValue.toString()
                downloadSoundQuality.summary = getSummary(
                    value,
                    R.array.sound_quality_entries,
                    R.array.sound_quality_entry_values
                )
                true
            }
        }

        private fun initFilter() {
            filterSize.summary = getSummary(
                ConfigPreferences.filterSize,
                R.array.filter_size_entries,
                R.array.filter_size_entry_values
            )
            filterSize.setOnPreferenceChangeListener { preference, newValue ->
                val value = newValue.toString()
                filterSize.summary = getSummary(
                    value,
                    R.array.filter_size_entries,
                    R.array.filter_size_entry_values
                )
                true
            }

            filterTime.summary = getSummary(
                ConfigPreferences.filterTime,
                R.array.filter_time_entries,
                R.array.filter_time_entry_values
            )
            filterTime.setOnPreferenceChangeListener { preference, newValue ->
                val value = newValue.toString()
                filterTime.summary = getSummary(
                    value,
                    R.array.filter_time_entries,
                    R.array.filter_time_entry_values
                )
                true
            }
        }

        private fun startEqualizer() {
            if (MusicUtils.isAudioControlPanelAvailable(requireContext())) {
                val intent = Intent()
                val packageName = requireContext().packageName
                intent.action = AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL
                intent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, packageName)
                intent.putExtra(AudioEffect.EXTRA_CONTENT_TYPE, AudioEffect.CONTENT_TYPE_MUSIC)
                intent.putExtra(
                    AudioEffect.EXTRA_AUDIO_SESSION,
                    playerController.getAudioSessionId()
                )
                try {
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                    toast(R.string.device_not_support)
                }
            } else {
                toast(R.string.device_not_support)
            }
        }

        private fun getSummary(value: String, entries: Int, values: Int): String {
            val entryArray = resources.getStringArray(entries)
            val valueArray = resources.getStringArray(values)
            val index = valueArray.indexOf(value).coerceAtLeast(0)
            return entryArray[index]
        }
    }
}