package me.wcy.music.utils

import android.view.View
import me.wcy.music.enums.LoadStateEnum

/**
 * 视图工具类
 * Created by hzwangchenyan on 2016/1/14.
 */
object ViewUtils {
    fun changeViewState(success: View?, loading: View?, fail: View?, state: LoadStateEnum?) {
        when (state) {
            LoadStateEnum.LOADING -> {
                success!!.visibility = View.GONE
                loading!!.visibility = View.VISIBLE
                fail!!.visibility = View.GONE
            }

            LoadStateEnum.LOAD_SUCCESS -> {
                success!!.visibility = View.VISIBLE
                loading!!.visibility = View.GONE
                fail!!.visibility = View.GONE
            }

            LoadStateEnum.LOAD_FAIL -> {
                success!!.visibility = View.GONE
                loading!!.visibility = View.GONE
                fail!!.visibility = View.VISIBLE
            }

            else -> {}
        }
    }
}