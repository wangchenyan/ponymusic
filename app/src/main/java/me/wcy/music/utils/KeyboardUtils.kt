package me.wcy.music.utils

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment

/**
 * Created by hzwangchenyan on 2017/6/2.
 */
object KeyboardUtils {
    fun showKeyboard(view: View?) {
        if (view == null) {
            return
        }
        view.isFocusable = true
        view.isFocusableInTouchMode = true
        view.requestFocus()
        view.postDelayed(Runnable {
            val imm =
                view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(view, 0)
        }, 200L)
    }

    fun hideKeyboard(fragment: Fragment?) {
        if (fragment == null) {
            return
        }
        hideKeyboard(fragment.activity)
    }

    fun hideKeyboard(activity: Activity?) {
        if (activity == null || activity.currentFocus == null) {
            return
        }
        val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(
            activity.currentFocus!!.windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS
        )
    }

    fun hideKeyboard(view: View?) {
        if (view == null) {
            return
        }
        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }
}