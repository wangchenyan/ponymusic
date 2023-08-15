package me.wcy.music.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import me.wcy.music.R

/**
 * 在线音乐
 * Created by wcy on 2015/11/26.
 */
class SheetListFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sheet_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun setListener() {
    }

    override fun onSaveInstanceState(outState: Bundle) {
    }

    fun onRestoreInstanceState(savedInstanceState: Bundle) {
    }
}