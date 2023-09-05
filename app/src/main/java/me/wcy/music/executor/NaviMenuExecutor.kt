package me.wcy.music.executor

import android.content.DialogInterface
import android.content.Intent
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import com.blankj.utilcode.util.AppUtils
import me.wcy.common.ext.toast
import me.wcy.music.R
import me.wcy.music.activity.AboutActivity
import me.wcy.music.activity.MusicActivity
import me.wcy.music.activity.SettingActivity
import me.wcy.music.service.QuitTimer

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

            R.id.action_timer -> {
                timerDialog()
                return true
            }

            R.id.action_exit -> {
                AppUtils.exitApp()
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
        QuitTimer.start((minute * 60 * 1000).toLong())
        if (minute > 0) {
            toast(activity.getString(R.string.timer_set, minute.toString()))
        } else {
            toast(R.string.timer_cancel)
        }
    }
}