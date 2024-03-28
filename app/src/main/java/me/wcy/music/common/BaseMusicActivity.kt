package me.wcy.music.common

import com.kingja.loadsir.callback.Callback
import me.wcy.music.widget.loadsir.SoundWaveLoadingCallback
import top.wangchenyan.common.ui.activity.BaseActivity

/**
 * Created by wangchenyan.top on 2023/9/4.
 */
abstract class BaseMusicActivity : BaseActivity() {

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