package me.wcy.music.widget

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.AbsListView
import android.widget.ListView
import me.wcy.music.R
import me.wcy.music.widget.AutoLoadListView

/**
 * 自动加载更多ListView
 * Created by hzwangchenyan on 2016/1/7.
 */
class AutoLoadListView : ListView, AbsListView.OnScrollListener {
    private var vFooter: View? = null
    private var mListener: OnLoadListener? = null
    private var mFirstVisibleItem = 0
    private var mEnableLoad = true
    private var mIsLoading = false

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        vFooter = LayoutInflater.from(context).inflate(R.layout.auto_load_list_view_footer, null)
        addFooterView(vFooter, null, false)
        setOnScrollListener(this)
        onLoadComplete()
    }

    fun setOnLoadListener(listener: OnLoadListener?) {
        mListener = listener
    }

    fun onLoadComplete() {
        Log.d(TAG, "onLoadComplete")
        mIsLoading = false
        removeFooterView(vFooter)
    }

    fun setEnable(enable: Boolean) {
        mEnableLoad = enable
    }

    override fun onScroll(
        view: AbsListView,
        firstVisibleItem: Int,
        visibleItemCount: Int,
        totalItemCount: Int
    ) {
        val isPullDown = firstVisibleItem > mFirstVisibleItem
        if (mEnableLoad && !mIsLoading && isPullDown) {
            val lastVisibleItem = firstVisibleItem + visibleItemCount
            if (lastVisibleItem >= totalItemCount - 1) {
                onLoad()
            }
        }
        mFirstVisibleItem = firstVisibleItem
    }

    override fun onScrollStateChanged(view: AbsListView, scrollState: Int) {}
    private fun onLoad() {
        Log.d(TAG, "onLoad")
        mIsLoading = true
        addFooterView(vFooter, null, false)
        if (mListener != null) {
            mListener!!.onLoad()
        }
    }

    interface OnLoadListener {
        fun onLoad()
    }

    companion object {
        private val TAG = AutoLoadListView::class.java.simpleName
    }
}