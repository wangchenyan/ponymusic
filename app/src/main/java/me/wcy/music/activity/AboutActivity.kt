package me.wcy.music.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.preference.Preference
import android.preference.Preference.OnPreferenceClickListener
import android.preference.PreferenceFragment
import me.wcy.music.BuildConfig
import me.wcy.music.R

class AboutActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        fragmentManager.beginTransaction().replace(R.id.ll_fragment_container, AboutFragment())
            .commit()
    }

    class AboutFragment : PreferenceFragment(), OnPreferenceClickListener {
        private var mVersion: Preference? = null
        private var mShare: Preference? = null
        private var mStar: Preference? = null
        private var mWeibo: Preference? = null
        private var mBlog: Preference? = null
        private var mGithub: Preference? = null
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.preference_about)
            mVersion = findPreference("version")
            mShare = findPreference("share")
            mStar = findPreference("star")
            mWeibo = findPreference("weibo")
            mBlog = findPreference("blog")
            mGithub = findPreference("github")
            mVersion?.summary = "v " + BuildConfig.VERSION_NAME
            setListener()
        }

        private fun setListener() {
            mShare!!.onPreferenceClickListener = this
            mStar!!.onPreferenceClickListener = this
            mWeibo!!.onPreferenceClickListener = this
            mBlog!!.onPreferenceClickListener = this
            mGithub!!.onPreferenceClickListener = this
        }

        override fun onPreferenceClick(preference: Preference): Boolean {
            if (preference === mShare) {
                share()
                return true
            } else if (preference === mStar) {
                openUrl(getString(R.string.about_project_url))
                return true
            } else if (preference === mWeibo || preference === mBlog || preference === mGithub) {
                openUrl(preference.summary.toString())
                return true
            }
            return false
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