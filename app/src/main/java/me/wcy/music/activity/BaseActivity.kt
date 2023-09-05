package me.wcy.music.activity

import android.app.ProgressDialog
import android.content.ServiceConnection
import android.graphics.Color
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import me.wcy.music.R
import me.wcy.music.utils.binding.ViewBinder

/**
 * 基类<br></br>
 * 如果继承本类，需要在 layout 中添加 [Toolbar] ，并将 AppTheme 继承 Theme.AppCompat.NoActionBar 。
 * Created by wcy on 2015/11/26.
 */
abstract class BaseActivity : AppCompatActivity() {
    protected var handler: Handler? = null
    private var serviceConnection: ServiceConnection? = null
    private var progressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSystemBarTransparent()
        volumeControlStream = AudioManager.STREAM_MUSIC
        handler = Handler(Looper.getMainLooper())
    }

    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)
        initView()
    }

    override fun setContentView(view: View) {
        super.setContentView(view)
        initView()
    }

    override fun setContentView(view: View, params: ViewGroup.LayoutParams) {
        super.setContentView(view, params)
        initView()
    }

    private fun initView() {
        ViewBinder.bind(this)
        val mToolbar = findViewById<Toolbar>(R.id.toolbar)
            ?: throw IllegalStateException("Layout is required to include a Toolbar with id 'toolbar'")
        setSupportActionBar(mToolbar)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun setSystemBarTransparent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // LOLLIPOP解决方案
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.TRANSPARENT
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // KITKAT解决方案
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    @JvmOverloads
    fun showProgress(message: String? = getString(R.string.loading)) {
        if (progressDialog == null) {
            progressDialog = ProgressDialog(this)
            progressDialog!!.setCancelable(false)
        }
        progressDialog!!.setMessage(message)
        if (!progressDialog!!.isShowing) {
            progressDialog!!.show()
        }
    }

    fun cancelProgress() {
        if (progressDialog != null && progressDialog!!.isShowing) {
            progressDialog!!.cancel()
        }
    }

    override fun onDestroy() {
        if (serviceConnection != null) {
            unbindService(serviceConnection!!)
        }
        super.onDestroy()
    }
}