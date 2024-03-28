package me.wcy.music.storage.preference

import com.blankj.utilcode.util.StringUtils
import me.wcy.music.R
import me.wcy.music.common.DarkModeService
import me.wcy.music.consts.PreferenceName
import top.wangchenyan.common.CommonApp
import top.wangchenyan.common.storage.IPreferencesFile
import top.wangchenyan.common.storage.PreferencesFile

/**
 * SharedPreferences工具类
 * Created by wcy on 2015/11/28.
 */
object ConfigPreferences :
    IPreferencesFile by PreferencesFile(CommonApp.app, PreferenceName.CONFIG, false) {

    var playSoundQuality by IPreferencesFile.StringProperty(
        StringUtils.getString(R.string.setting_key_play_sound_quality),
        "standard"
    )

    var downloadSoundQuality by IPreferencesFile.StringProperty(
        StringUtils.getString(R.string.setting_key_download_sound_quality),
        "standard"
    )

    var filterSize by IPreferencesFile.StringProperty(
        StringUtils.getString(R.string.setting_key_filter_size),
        "0"
    )

    var filterTime by IPreferencesFile.StringProperty(
        StringUtils.getString(R.string.setting_key_filter_time),
        "0"
    )

    var darkMode by IPreferencesFile.StringProperty(
        "dark_mode",
        DarkModeService.DarkMode.Auto.value
    )

    var playMode: Int by IPreferencesFile.IntProperty("play_mode", 0)

    var currentSongId: String by IPreferencesFile.StringProperty("current_song_id", "")

    var apiDomain: String by IPreferencesFile.StringProperty("api_domain", "")
}