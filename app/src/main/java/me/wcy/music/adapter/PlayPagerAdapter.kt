package me.wcy.music.adapter

import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter

/**
 * 正在播放ViewPager适配器，包含歌词和封面
 * Created by wcy on 2015/11/30.
 */
class PlayPagerAdapter(private val mViews: List<View>) : PagerAdapter() {
    override fun getCount(): Int {
        return mViews.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        container.addView(mViews[position])
        return mViews[position]
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(mViews[position])
    }
}