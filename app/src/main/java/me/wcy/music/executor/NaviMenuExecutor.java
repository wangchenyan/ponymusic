package me.wcy.music.executor;

import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;

import me.wcy.music.R;
import me.wcy.music.activity.SearchMusicActivity;

/**
 * 导航菜单执行器
 * Created by hzwangchenyan on 2016/1/14.
 */
public class NaviMenuExecutor {
    private Context mContext;

    public static NaviMenuExecutor getInstance() {
        return SingletonHolder.sInstance;
    }

    public NaviMenuExecutor setContext(Context context) {
        mContext = context;
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
                intent = new Intent(mContext, SearchMusicActivity.class);
                mContext.startActivity(intent);
                return true;
            case R.id.action_setting:
                return true;
            case R.id.action_share:
                share();
                return true;
            case R.id.action_about:
                return true;
        }
        return false;
    }

    private void share() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, mContext.getString(R.string.share_app, mContext.getString(R.string.app_name)));
        mContext.startActivity(Intent.createChooser(intent, mContext.getString(R.string.share)));
    }
}
