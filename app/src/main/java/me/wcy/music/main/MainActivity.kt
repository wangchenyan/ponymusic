package me.wcy.music.main

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import me.wcy.music.R
import me.wcy.music.account.service.UserService
import me.wcy.music.common.ApiDomainDialog
import me.wcy.music.common.BaseMusicActivity
import me.wcy.music.consts.RoutePath
import me.wcy.music.databinding.ActivityMainBinding
import me.wcy.music.databinding.NavigationHeaderBinding
import me.wcy.music.databinding.TabItemBinding
import me.wcy.music.service.MusicService
import me.wcy.music.service.PlayServiceModule
import me.wcy.music.service.PlayServiceModule.playerController
import me.wcy.music.utils.QuitTimer
import me.wcy.music.utils.TimeUtils
import me.wcy.router.CRouter
import top.wangchenyan.common.ext.showConfirmDialog
import top.wangchenyan.common.ext.toast
import top.wangchenyan.common.ext.viewBindings
import top.wangchenyan.common.widget.pager.CustomTabPager
import javax.inject.Inject

/**
 * Created by wangchenyan.top on 2023/8/21.
 */
@AndroidEntryPoint
class MainActivity : BaseMusicActivity() {
    private val viewBinding by viewBindings<ActivityMainBinding>()
    private val quitTimer by lazy {
        QuitTimer(onTimerListener)
    }
    private var timerItem: MenuItem? = null

    @Inject
    lateinit var userService: UserService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        PlayServiceModule.isPlayerReady.observe(this) { isReady ->
            if (isReady) {
                setContentView(viewBinding.root)

                CustomTabPager(lifecycle, supportFragmentManager, viewBinding.viewPager).apply {
                    NaviTab.ALL.onEach {
                        val tabItem = getTabItem(it.icon, it.name)
                        addFragment(it.newFragment(), tabItem.root)
                    }
                    setScrollable(false)
                    setup()
                }

                initDrawer()
                parseIntent()
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        parseIntent()
    }

    private fun initDrawer() {
        val navigationHeaderBinding = NavigationHeaderBinding.inflate(
            LayoutInflater.from(this),
            viewBinding.navigationView,
            false
        )
        viewBinding.navigationView.addHeaderView(navigationHeaderBinding.root)
        viewBinding.navigationView.setNavigationItemSelectedListener(onMenuSelectListener)
        lifecycleScope.launch {
            userService.profile.collectLatest { profile ->
                val menuLogout = viewBinding.navigationView.menu.findItem(R.id.action_logout)
                menuLogout.isVisible = profile != null
            }
        }
    }

    fun openDrawer() {
        if (viewBinding.drawerLayout.isDrawerOpen(GravityCompat.START).not()) {
            viewBinding.drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    private fun parseIntent() {
        val intent = intent
        if (intent.hasExtra(MusicService.EXTRA_NOTIFICATION)) {
            if (application.playerController().currentSong.value != null) {
                CRouter.with(this).url(RoutePath.PLAYING).start()
            }
            setIntent(Intent())
        }
    }

    private val onMenuSelectListener = object : NavigationView.OnNavigationItemSelectedListener {
        override fun onNavigationItemSelected(item: MenuItem): Boolean {
            viewBinding.drawerLayout.closeDrawers()
            lifecycleScope.launch {
                delay(1000)
                item.isChecked = false
            }
            when (item.itemId) {
                R.id.action_domain_setting -> {
                    ApiDomainDialog(this@MainActivity).show()
                    return true
                }

                R.id.action_setting -> {
                    CRouter.with(this@MainActivity).url("/settings").start()
                    return true
                }

                R.id.action_timer -> {
                    timerDialog()
                    return true
                }

                R.id.action_logout -> {
                    logout()
                    return true
                }

                R.id.action_exit -> {
                    exitApp()
                    return true
                }

                R.id.action_about -> {
                    startActivity(Intent(this@MainActivity, AboutActivity::class.java))
                    return true
                }
            }
            return false
        }
    }

    private val onTimerListener = object : QuitTimer.OnTimerListener {
        override fun onTick(remain: Long) {
            if (timerItem == null) {
                timerItem = viewBinding.navigationView.menu.findItem(R.id.action_timer)
            }
            val title = getString(R.string.menu_timer)
            timerItem?.title = if (remain == 0L) {
                title
            } else {
                TimeUtils.formatTime("$title(mm:ss)", remain)
            }
        }

        override fun onTimeEnd() {
            exitApp()
        }
    }

    private fun timerDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.menu_timer)
            .setItems(resources.getStringArray(R.array.timer_text)) { dialog: DialogInterface?, which: Int ->
                val times = resources.getIntArray(R.array.timer_int)
                startTimer(times[which])
            }
            .show()
    }

    private fun startTimer(minute: Int) {
        quitTimer.start((minute * 60 * 1000).toLong())
        if (minute > 0) {
            toast(getString(R.string.timer_set, minute.toString()))
        } else {
            toast(R.string.timer_cancel)
        }
    }

    private fun logout() {
        showConfirmDialog(message = "确认退出登录？") {
            lifecycleScope.launch {
                userService.logout()
            }
        }
    }

    private fun exitApp() {
        application.playerController().stop()
        finish()
    }

    private fun getTabItem(@DrawableRes icon: Int, text: CharSequence): TabItemBinding {
        val binding = TabItemBinding.inflate(layoutInflater, viewBinding.tabBar, true)
        binding.ivIcon.setImageResource(icon)
        binding.tvTitle.text = text
        return binding
    }

    override fun getNavigationBarColor(): Int {
        return R.color.tab_bg
    }

    override fun onDestroy() {
        super.onDestroy()
        quitTimer.stop()
    }
}