package me.wcy.music.executor;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.MenuItem;

import me.wcy.music.R;
import me.wcy.music.activity.AboutActivity;
import me.wcy.music.activity.MusicActivity;
import me.wcy.music.activity.SearchMusicActivity;
import me.wcy.music.activity.SettingActivity;
import me.wcy.music.service.PlayService;

/**
 * 导航菜单执行器
 * Created by hzwangchenyan on 2016/1/14.
 */
public class NaviMenuExecutor {

    public static boolean onNavigationItemSelected(MenuItem item, Context context) {
        switch (item.getItemId()) {
            case R.id.action_search:
                startActivity(context, SearchMusicActivity.class);
                return true;
            case R.id.action_setting:
                startActivity(context, SettingActivity.class);
                return true;
            case R.id.action_share:
                share(context);
                return true;
            case R.id.action_praise:
                praise(context);
                return true;
            case R.id.action_about:
                startActivity(context, AboutActivity.class);
                return true;
            case R.id.action_exit:
                exit(context);
                return true;
        }
        return false;
    }

    private static void startActivity(Context context, Class<?> cls) {
        Intent intent = new Intent(context, cls);
        context.startActivity(intent);
    }

    private static void praise(Context context) {
        Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        context.startActivity(intent);
    }

    private static void share(Context context) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.share_app, context.getString(R.string.app_name)));
        context.startActivity(Intent.createChooser(intent, context.getString(R.string.share)));
    }

    private static void exit(Context context) {
        if (context instanceof MusicActivity) {
            MusicActivity activity = (MusicActivity) context;
            PlayService service = activity.getPlayService();
            activity.finish();
            service.stop();
        }
    }
}
