package me.wcy.music.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.google.android.material.navigation.NavigationView
import me.wcy.common.permission.Permissioner
import me.wcy.music.R
import me.wcy.music.adapter.FragmentAdapter
import me.wcy.music.constants.Extras
import me.wcy.music.constants.Keys
import me.wcy.music.executor.ControlPanel
import me.wcy.music.executor.NaviMenuExecutor
import me.wcy.music.fragment.LocalMusicFragment
import me.wcy.music.fragment.PlayFragment
import me.wcy.music.fragment.SheetListFragment
import me.wcy.music.service.AudioPlayer
import me.wcy.music.service.PlayService
import me.wcy.music.service.QuitTimer
import me.wcy.music.service.QuitTimer.OnTimerListener
import me.wcy.music.utils.SystemUtils
import me.wcy.music.utils.binding.Bind

class MusicActivity : BaseActivity(), View.OnClickListener, OnTimerListener,
    NavigationView.OnNavigationItemSelectedListener, OnPageChangeListener {
    @Bind(R.id.drawer_layout)
    private val drawerLayout: DrawerLayout? = null

    @Bind(R.id.navigation_view)
    private val navigationView: NavigationView? = null

    @Bind(R.id.iv_menu)
    private val ivMenu: ImageView? = null

    @Bind(R.id.iv_search)
    private val ivSearch: ImageView? = null

    @Bind(R.id.tv_local_music)
    private val tvLocalMusic: TextView? = null

    @Bind(R.id.tv_online_music)
    private val tvOnlineMusic: TextView? = null

    @Bind(R.id.viewpager)
    private val mViewPager: ViewPager? = null

    @Bind(R.id.fl_play_bar)
    private val flPlayBar: FrameLayout? = null
    private var vNavigationHeader: View? = null
    private var mLocalMusicFragment: LocalMusicFragment? = null
    private var mSheetListFragment: SheetListFragment? = null
    private var mPlayFragment: PlayFragment? = null
    private val controlPanel: ControlPanel by lazy {
        ControlPanel(flPlayBar)
    }
    private var naviMenuExecutor: NaviMenuExecutor? = null
    private var timerItem: MenuItem? = null
    private var isPlayFragmentShow = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_music)

        val intent = Intent(this, PlayService::class.java)
        startService(intent)

        Permissioner.requestNotificationPermission(this, null)

        setupView()
        naviMenuExecutor = NaviMenuExecutor(this)
        AudioPlayer.get().addOnPlayEventListener(controlPanel)
        QuitTimer.get().setOnTimerListener(this)
        parseIntent()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        parseIntent()
    }

    private fun setupView() {
        // add navigation header
        vNavigationHeader =
            LayoutInflater.from(this).inflate(R.layout.navigation_header, navigationView, false)
        navigationView!!.addHeaderView(vNavigationHeader!!)

        // setup view pager
        mLocalMusicFragment = LocalMusicFragment()
        mSheetListFragment = SheetListFragment()
        val adapter = FragmentAdapter(supportFragmentManager)
        adapter.addFragment(mLocalMusicFragment!!)
        adapter.addFragment(mSheetListFragment!!)
        mViewPager!!.adapter = adapter
        tvLocalMusic!!.isSelected = true
        ivMenu!!.setOnClickListener(this)
        ivSearch!!.setOnClickListener(this)
        tvLocalMusic.setOnClickListener(this)
        tvOnlineMusic!!.setOnClickListener(this)
        flPlayBar!!.setOnClickListener(this)
        mViewPager.addOnPageChangeListener(this)
        navigationView.setNavigationItemSelectedListener(this)
    }

    private fun parseIntent() {
        val intent = intent
        if (intent.hasExtra(Extras.EXTRA_NOTIFICATION)) {
            showPlayingFragment()
            setIntent(Intent())
        }
    }

    override fun onTimer(remain: Long) {
        if (timerItem == null) {
            timerItem = navigationView!!.menu.findItem(R.id.action_timer)
        }
        val title = getString(R.string.menu_timer)
        timerItem!!.title = if (remain == 0L) title else SystemUtils.formatTime(
            "$title(mm:ss)",
            remain
        )
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.iv_menu -> drawerLayout!!.openDrawer(GravityCompat.START)
            R.id.iv_search -> {}
            R.id.tv_local_music -> mViewPager!!.currentItem = 0
            R.id.tv_online_music -> mViewPager!!.currentItem = 1
            R.id.fl_play_bar -> showPlayingFragment()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        drawerLayout!!.closeDrawers()
        handler!!.postDelayed({ item.isChecked = false }, 500)
        return naviMenuExecutor!!.onNavigationItemSelected(item)
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
    override fun onPageSelected(position: Int) {
        if (position == 0) {
            tvLocalMusic!!.isSelected = true
            tvOnlineMusic!!.isSelected = false
        } else {
            tvLocalMusic!!.isSelected = false
            tvOnlineMusic!!.isSelected = true
        }
    }

    override fun onPageScrollStateChanged(state: Int) {}
    private fun showPlayingFragment() {
        if (isPlayFragmentShow) {
            return
        }
        val ft = supportFragmentManager.beginTransaction()
        ft.setCustomAnimations(R.anim.fragment_slide_up, 0)
        if (mPlayFragment == null) {
            mPlayFragment = PlayFragment()
            ft.replace(android.R.id.content, mPlayFragment!!)
        } else {
            ft.show(mPlayFragment!!)
        }
        ft.commitAllowingStateLoss()
        isPlayFragmentShow = true
    }

    private fun hidePlayingFragment() {
        val ft = supportFragmentManager.beginTransaction()
        ft.setCustomAnimations(0, R.anim.fragment_slide_down)
        ft.hide(mPlayFragment!!)
        ft.commitAllowingStateLoss()
        isPlayFragmentShow = false
    }

    override fun onBackPressed() {
        if (mPlayFragment != null && isPlayFragmentShow) {
            hidePlayingFragment()
            return
        }
        if (drawerLayout!!.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers()
            return
        }
        super.onBackPressed()
    }

    @SuppressLint("MissingSuperCall")
    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(Keys.VIEW_PAGER_INDEX, mViewPager!!.currentItem)
        mLocalMusicFragment?.onSaveInstanceState(outState)
        mSheetListFragment?.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        mViewPager?.post {
            mViewPager.setCurrentItem(
                savedInstanceState.getInt(Keys.VIEW_PAGER_INDEX),
                false
            )
            mLocalMusicFragment?.onRestoreInstanceState(savedInstanceState)
            mSheetListFragment?.onRestoreInstanceState(savedInstanceState)
        }
    }

    override fun onDestroy() {
        AudioPlayer.get().removeOnPlayEventListener(controlPanel)
        QuitTimer.get().setOnTimerListener(null)
        super.onDestroy()
    }
}