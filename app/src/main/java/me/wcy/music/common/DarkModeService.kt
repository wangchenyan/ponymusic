package me.wcy.music.common

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import com.blankj.utilcode.util.ActivityUtils
import me.wcy.music.storage.preference.ConfigPreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DarkModeService @Inject constructor() {

    fun init() {
        setDarkModeInternal(DarkMode.fromValue(ConfigPreferences.darkMode))
    }

    fun setDarkMode(mode: DarkMode) {
        if (mode.value != ConfigPreferences.darkMode) {
            ConfigPreferences.darkMode = mode.value
            setDarkModeInternal(mode)
        }
    }

    private fun setDarkModeInternal(mode: DarkMode) {
        AppCompatDelegate.setDefaultNightMode(mode.systemValue)
    }

    fun isDarkMode(): Boolean {
        val context = ActivityUtils.getTopActivity() ?: return false
        val nightModeFlags =
            context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        when (nightModeFlags) {
            Configuration.UI_MODE_NIGHT_NO -> {
                return false
            }

            Configuration.UI_MODE_NIGHT_YES -> {
                return true
            }
        }
        return false
    }

    sealed class DarkMode(val value: String, val systemValue: Int) {
        object Auto : DarkMode("0", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        object Light : DarkMode("1", AppCompatDelegate.MODE_NIGHT_NO)
        object Dark : DarkMode("2", AppCompatDelegate.MODE_NIGHT_YES)

        override fun hashCode(): Int {
            return value.hashCode()
        }

        override fun equals(other: Any?): Boolean {
            return other is DarkMode && other.value == this.value
        }

        companion object {
            fun fromValue(value: String): DarkMode {
                return when (value) {
                    "1" -> Light
                    "2" -> Dark
                    else -> Auto
                }
            }
        }
    }
}