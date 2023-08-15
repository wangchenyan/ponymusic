package me.wcy.music.executor

import android.content.DialogInterface
import android.content.Intent
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import me.wcy.music.R
import me.wcy.music.activity.AboutActivity
import me.wcy.music.activity.MusicActivity
import me.wcy.music.activity.SettingActivity
import me.wcy.music.constants.Actions
import me.wcy.music.service.PlayService
import me.wcy.music.service.QuitTimer
import me.wcy.music.storage.preference.Preferences
import me.wcy.music.utils.ToastUtils

/**
 * 导航菜单执行器
 * Created by hzwangchenyan on 2016/1/14.
 */
class NaviMenuExecutor(private val activity: MusicActivity) {
    fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_setting -> {
                startActivity(SettingActivity::class.java)
                return true
            }

            R.id.action_night -> nightMode()
            R.id.action_timer -> {
                timerDialog()
                return true
            }

            R.id.action_exit -> {
                activity.finish()
                PlayService.startCommand(activity, Actions.ACTION_STOP)
                return true
            }

            R.id.action_about -> {
                startActivity(AboutActivity::class.java)
                return true
            }
        }
        return false
    }

    private fun startActivity(cls: Class<*>) {
        val intent = Intent(activity, cls)
        activity.startActivity(intent)
    }

    private fun nightMode() {
        Preferences.saveNightMode(!Preferences.isNightMode())
        activity.recreate()
    }

    private fun timerDialog() {
        AlertDialog.Builder(activity)
            .setTitle(R.string.menu_timer)
            .setItems(activity.resources.getStringArray(R.array.timer_text)) { dialog: DialogInterface?, which: Int ->
                val times = activity.resources.getIntArray(R.array.timer_int)
                startTimer(times[which])
            }
            .show()
    }

    private fun startTimer(minute: Int) {
        QuitTimer.get().start((minute * 60 * 1000).toLong())
        if (minute > 0) {
            ToastUtils.show(activity.getString(R.string.timer_set, minute.toString()))
        } else {
            ToastUtils.show(R.string.timer_cancel)
        }
    }
}