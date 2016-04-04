package me.wcy.music.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import me.wcy.music.R;
import me.wcy.music.utils.SystemUtils;
import me.wcy.music.utils.UpdateUtils;

public class AboutActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        getFragmentManager().beginTransaction().replace(R.id.ll_fragment_container, new AboutFragment()).commit();
    }

    @Override
    protected void setListener() {
    }

    public static class AboutFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {
        private Preference mVersion;
        private Preference mUpdate;
        private Preference mStar;
        private Preference mWeibo;
        private Preference mJianshu;
        private Preference mGithub;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preference_about);

            mVersion = findPreference("version");
            mUpdate = findPreference("update");
            mStar = findPreference("star");
            mWeibo = findPreference("weibo");
            mJianshu = findPreference("jianshu");
            mGithub = findPreference("github");

            mVersion.setSummary("v " + SystemUtils.getVersionName(getActivity()));
            setListener();
        }

        private void setListener() {
            mUpdate.setOnPreferenceClickListener(this);
            mStar.setOnPreferenceClickListener(this);
            mWeibo.setOnPreferenceClickListener(this);
            mJianshu.setOnPreferenceClickListener(this);
            mGithub.setOnPreferenceClickListener(this);
        }

        @Override
        public boolean onPreferenceClick(Preference preference) {
            if (preference == mUpdate) {
                UpdateUtils.checkUpdate(getActivity());
                return true;
            } else if (preference == mStar) {
                openUrl(getString(R.string.about_project_url));
                return true;
            } else if (preference == mWeibo || preference == mJianshu || preference == mGithub) {
                openUrl(preference.getSummary().toString());
                return true;
            }
            return false;
        }

        private void openUrl(String url) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        }
    }
}
