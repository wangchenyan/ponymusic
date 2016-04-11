package me.wcy.music.executor;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.MenuItem;

import me.wcy.music.R;
import me.wcy.music.activity.AboutActivity;
import me.wcy.music.activity.MusicActivity;
import me.wcy.music.activity.SearchMusicActivity;
import me.wcy.music.activity.SettingActivity;
import me.wcy.music.service.PlayService;
import me.wcy.music.utils.ToastUtils;

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
            case R.id.action_timer:
                timerDialog(context);
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

    private static void timerDialog(final Context context) {
        new AlertDialog.Builder(context)
                .setTitle(R.string.menu_timer)
                .setItems(context.getResources().getStringArray(R.array.timer_text), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int[] times = context.getResources().getIntArray(R.array.timer_int);
                        startTimer(context, times[which]);
                    }
                })
                .show();
    }

    private static void startTimer(Context context, int minute) {
        if (context instanceof MusicActivity) {
            MusicActivity activity = (MusicActivity) context;
            PlayService service = activity.getPlayService();
            service.startQuitTimer(minute * 60 * 1000);
            if (minute > 0) {
                ToastUtils.show(context.getString(R.string.timer_set, minute));
            } else {
                ToastUtils.show(R.string.timer_cancel);
            }
        }
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
