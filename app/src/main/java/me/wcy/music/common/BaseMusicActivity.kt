package me.wcy.music.common

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.blankj.utilcode.util.AppUtils
import com.kingja.loadsir.callback.Callback
import me.wcy.music.service.PlayService
import me.wcy.music.widget.loadsir.SoundWaveLoadingCallback
import top.wangchenyan.common.ui.activity.BaseActivity

/**
 * Created by wangchenyan.top on 2023/9/4.
 */
abstract class BaseMusicActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        runCatching {
            startService(Intent(this, PlayService::class.java))
        }.onFailure {
            Log.e(TAG, "startService error", it)
            AppUtils.exitApp()
        }
    }

    override fun getLoadingCallback(): Callback {
        return SoundWaveLoadingCallback()
    }

    override fun showLoadSirLoading() {
        loadService?.showCallback(SoundWaveLoadingCallback::class.java)
    }

    companion object {
        private const val TAG = "BaseMusicActivity"
    }
}