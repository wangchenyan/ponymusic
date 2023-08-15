package me.wcy.music.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import com.hwangjr.rxbus.RxBus
import me.wcy.music.utils.binding.ViewBinder

/**
 * 基类<br></br>
 * Created by wcy on 2015/11/26.
 */
abstract class BaseFragment : Fragment() {
    protected var handler: Handler? = null
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        handler = Handler(Looper.getMainLooper())
        ViewBinder.bind(this, view)
        RxBus.get().register(this)
    }

    override fun onStart() {
        super.onStart()
        setListener()
    }

    protected open fun setListener() {}

    override fun onDestroy() {
        RxBus.get().unregister(this)
        super.onDestroy()
    }
}