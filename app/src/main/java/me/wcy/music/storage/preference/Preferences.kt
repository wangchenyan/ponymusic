package me.wcy.music.storage.preference

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import me.wcy.music.R

/**
 * SharedPreferences工具类
 * Created by wcy on 2015/11/28.
 */
@SuppressLint("StaticFieldLeak")
object Preferences {
    private const val PLAY_POSITION = "play_position"
    private const val PLAY_MODE = "play_mode"
    private const val SPLASH_URL = "splash_url"
    private const val NIGHT_MODE = "night_mode"
    private var sContext: Context? = null
    fun init(context: Context?) {
        sContext = context!!.applicationContext
    }

    val playPosition: Int
        get() = getInt(PLAY_POSITION, 0)

    fun savePlayPosition(position: Int) {
        saveInt(PLAY_POSITION, position)
    }

    val playMode: Int
        get() = getInt(PLAY_MODE, 0)

    fun savePlayMode(mode: Int) {
        saveInt(PLAY_MODE, mode)
    }

    val splashUrl: String?
        get() = getString(SPLASH_URL, "")

    fun saveSplashUrl(url: String?) {
        saveString(SPLASH_URL, url)
    }

    fun enableMobileNetworkPlay(): Boolean {
        return getBoolean(sContext!!.getString(R.string.setting_key_mobile_network_play), false)
    }

    fun saveMobileNetworkPlay(enable: Boolean) {
        saveBoolean(sContext!!.getString(R.string.setting_key_mobile_network_play), enable)
    }

    fun enableMobileNetworkDownload(): Boolean {
        return getBoolean(sContext!!.getString(R.string.setting_key_mobile_network_download), false)
    }

    fun isNightMode(): Boolean = getBoolean(NIGHT_MODE, false)

    fun saveNightMode(on: Boolean) {
        saveBoolean(NIGHT_MODE, on)
    }

    val filterSize: String?
        get() = getString(sContext!!.getString(R.string.setting_key_filter_size), "0")

    fun saveFilterSize(value: String?) {
        saveString(sContext!!.getString(R.string.setting_key_filter_size), value)
    }

    val filterTime: String?
        get() = getString(sContext!!.getString(R.string.setting_key_filter_time), "0")

    fun saveFilterTime(value: String?) {
        saveString(sContext!!.getString(R.string.setting_key_filter_time), value)
    }

    private fun getBoolean(key: String, defValue: Boolean): Boolean {
        return preferences.getBoolean(key, defValue)
    }

    private fun saveBoolean(key: String, value: Boolean) {
        preferences.edit().putBoolean(key, value).apply()
    }

    private fun getInt(key: String, defValue: Int): Int {
        return preferences.getInt(key, defValue)
    }

    private fun saveInt(key: String, value: Int) {
        preferences.edit().putInt(key, value).apply()
    }

    private fun getLong(key: String, defValue: Long): Long {
        return preferences.getLong(key, defValue)
    }

    private fun saveLong(key: String, value: Long) {
        preferences.edit().putLong(key, value).apply()
    }

    private fun getString(key: String, defValue: String?): String? {
        return preferences.getString(key, defValue)
    }

    private fun saveString(key: String, value: String?) {
        preferences.edit().putString(key, value).apply()
    }

    private val preferences: SharedPreferences
        private get() = PreferenceManager.getDefaultSharedPreferences(sContext)
}