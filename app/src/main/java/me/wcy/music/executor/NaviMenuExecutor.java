package me.wcy.music.executor;

import android.content.Intent;
import android.net.Uri;
import android.view.MenuItem;

import me.wcy.music.R;
import me.wcy.music.activity.AboutActivity;
import me.wcy.music.activity.MusicActivity;
import me.wcy.music.activity.SearchMusicActivity;
import me.wcy.music.activity.SettingActivity;

/**
 * 导航菜单执行器
 * Created by hzwangchenyan on 2016/1/14.
 */
public class NaviMenuExecutor {
    private MusicActivity mActivity;

    public static NaviMenuExecutor getInstance() {
        return SingletonHolder.sInstance;
    }

    public NaviMenuExecutor setContext(MusicActivity activity) {
        mActivity = activity;
        return this;
    }

    private NaviMenuExecutor() {
    }

    private static class SingletonHolder {
        private static NaviMenuExecutor sInstance = new NaviMenuExecutor();
    }

    public boolean onNavigationItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.action_search:
                intent = new Intent(mActivity, SearchMusicActivity.class);
                mActivity.startActivity(intent);
                return true;
            case R.id.action_setting:
                intent = new Intent(mActivity, SettingActivity.class);
                mActivity.startActivity(intent);
                return true;
            case R.id.action_share:
                share();
                return true;
            case R.id.action_praise:
                praise();
                return true;
            case R.id.action_about:
                intent = new Intent(mActivity, AboutActivity.class);
                mActivity.startActivity(intent);
                return true;
            case R.id.action_exit:
                exit();
                return true;
        }
        return false;
    }

    private void praise() {
        Uri uri = Uri.parse("market://details?id=" + mActivity.getPackageName());
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        mActivity.startActivity(intent);
    }

    private void share() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, mActivity.getString(R.string.share_app, mActivity.getString(R.string.app_name)));
        mActivity.startActivity(Intent.createChooser(intent, mActivity.getString(R.string.share)));
    }

    private void exit() {
        mActivity.getPlayService().stop();
        mActivity.finish();
    }
}
