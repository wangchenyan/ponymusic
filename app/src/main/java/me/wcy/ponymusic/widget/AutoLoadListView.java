package me.wcy.ponymusic.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import me.wcy.ponymusic.R;

/**
 * 自定义自动加载更多ListView
 * Created by hzwangchenyan on 2016/1/7.
 */
public class AutoLoadListView extends ListView implements AbsListView.OnScrollListener {
    private View vFooter;
    private OnLoadListener mListener;

    public AutoLoadListView(Context context) {
        super(context);
        init();
    }

    public AutoLoadListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AutoLoadListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        vFooter = LayoutInflater.from(getContext()).inflate(R.layout.auto_load_list_view_footer, null);
        vFooter.setVisibility(GONE);
        addFooterView(vFooter, null, false);
        setOnScrollListener(this);
    }

    public void setOnLoadListener(OnLoadListener listener) {
        mListener = listener;
    }

    public void onLoadComplete() {
        vFooter.setVisibility(GONE);
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (firstVisibleItem + visibleItemCount + 2 == totalItemCount) {
            onLoad();
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    private void onLoad() {
        vFooter.setVisibility(VISIBLE);
        if (mListener != null) {
            mListener.onLoad();
        }
    }
}
