package me.wcy.music.main

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.blankj.utilcode.util.AppUtils
import me.wcy.music.R
import top.wangchenyan.common.ui.activity.BaseActivity

class AboutActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, AboutFragment())
            .commit()
    }

    class AboutFragment : PreferenceFragmentCompat() {
        private val mVersion: Preference by lazy {
            findPreference("version")!!
        }
        private val mShare: Preference by lazy {
            findPreference("share")!!
        }
        private val mStar: Preference by lazy {
            findPreference("star")!!
        }
        private val mWeibo: Preference by lazy {
            findPreference("weibo")!!
        }
        private val mBlog: Preference by lazy {
            findPreference("blog")!!
        }
        private val mGithub: Preference by lazy {
            findPreference("github")!!
        }
        private val api: Preference by lazy {
            findPreference("api")!!
        }

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            addPreferencesFromResource(R.xml.preference_about)
            mVersion.summary = AppUtils.getAppVersionName()
            mShare.setOnPreferenceClickListener {
                share()
                true
            }
            mStar.setOnPreferenceClickListener {
                openUrl(getString(R.string.about_project_url))
                true
            }
            mWeibo.setOnPreferenceClickListener {
                openUrl(it.summary.toString())
                true
            }
            mBlog.setOnPreferenceClickListener {
                openUrl(it.summary.toString())
                true
            }
            mGithub.setOnPreferenceClickListener {
                openUrl(it.summary.toString())
                true
            }
            api.setOnPreferenceClickListener {
                openUrl("https://github.com/Binaryify/NeteaseCloudMusicApi")
                true
            }
        }

        private fun share() {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(
                Intent.EXTRA_TEXT,
                getString(
                    R.string.share_app,
                    getString(R.string.app_name),
                    getString(R.string.about_project_url)
                )
            )
            startActivity(Intent.createChooser(intent, getString(R.string.share)))
        }

        private fun openUrl(url: String) {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }
    }
}