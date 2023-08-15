package me.wcy.music.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

/**
 * Fragment适配器
 * Created by wcy on 2015/11/26.
 */
class FragmentAdapter(fm: FragmentManager?) : FragmentPagerAdapter(
    fm!!
) {
    private val mFragments: MutableList<Fragment> = ArrayList()
    fun addFragment(fragment: Fragment) {
        mFragments.add(fragment)
    }

    override fun getItem(position: Int): Fragment {
        return mFragments[position]
    }

    override fun getCount(): Int {
        return mFragments.size
    }
}