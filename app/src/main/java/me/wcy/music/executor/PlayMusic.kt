package me.wcy.music.executor

import android.app.Activity
import android.app.Dialog
import androidx.appcompat.app.AlertDialog
import me.wcy.music.R
import me.wcy.music.model.Music
import me.wcy.music.storage.preference.MusicPreferences

/**
 * Created by hzwangchenyan on 2017/1/20.
 */
abstract class PlayMusic(private val mActivity: Activity, private val mTotalStep: Int) :
    IExecutor<Music> {
    protected var music: Music? = null
    protected var mCounter = 0
    override fun execute() {
        checkNetwork()
    }

    private fun checkNetwork() {
        val mobileNetworkPlay = MusicPreferences.enableMobileNetworkPlay
        if (com.blankj.utilcode.util.NetworkUtils.isMobileData() && !mobileNetworkPlay) {
            val builder = AlertDialog.Builder(mActivity)
            builder.setTitle(R.string.tips)
            builder.setMessage(R.string.play_tips)
            builder.setPositiveButton(R.string.play_tips_sure) { dialog, which ->
                MusicPreferences.enableMobileNetworkPlay = true
                playInfoWrapper
            }
            builder.setNegativeButton(R.string.cancel, null)
            val dialog: Dialog = builder.create()
            dialog.setCanceledOnTouchOutside(false)
            dialog.show()
        } else {
            playInfoWrapper
        }
    }

    private val playInfoWrapper: Unit
        private get() {
            onPrepare()
            playInfo
        }
    protected abstract val playInfo: Unit
    protected fun checkCounter() {
        mCounter++
        if (mCounter == mTotalStep) {
            onExecuteSuccess(music!!)
        }
    }
}