package me.wcy.music.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import java.io.File;
import java.util.Calendar;

import me.wcy.music.R;
import me.wcy.music.application.AppCache;
import me.wcy.music.callback.JsonCallback;
import me.wcy.music.constants.Constants;
import me.wcy.music.model.JSplash;
import me.wcy.music.service.PlayService;
import me.wcy.music.utils.FileUtils;
import me.wcy.music.utils.Preferences;
import me.wcy.music.utils.ToastUtils;
import me.wcy.music.utils.binding.Bind;
import me.wcy.music.utils.permission.PermissionReq;
import me.wcy.music.utils.permission.PermissionResult;
import me.wcy.music.utils.permission.Permissions;
import okhttp3.Call;

public class SplashActivity extends BaseActivity {
    @Bind(R.id.iv_splash)
    private ImageView ivSplash;
    @Bind(R.id.tv_copyright)
    private TextView tvCopyright;
    private ServiceConnection mPlayServiceConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        int year = Calendar.getInstance().get(Calendar.YEAR);
        tvCopyright.setText(getString(R.string.copyright, year));

        checkService();
    }

    @Override
    protected void checkServiceAlive() {
        // SplashActivity不需要检查Service是否活着
    }

    private void checkService() {
        if (PlayService.isRunning(this)) {
            startMusicActivity();
            finish();
        } else {
            startService();
            initSplash();
            updateSplash();

            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    bindService();
                }
            }, 1000);
        }
    }

    private void startService() {
        Intent intent = new Intent();
        intent.setClass(this, PlayService.class);
        startService(intent);
    }

    private void bindService() {
        Intent intent = new Intent();
        intent.setClass(this, PlayService.class);
        mPlayServiceConnection = new PlayServiceConnection();
        bindService(intent, mPlayServiceConnection, Context.BIND_AUTO_CREATE);
    }

    private class PlayServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            final PlayService playService = ((PlayService.PlayBinder) service).getService();
            PermissionReq.with(SplashActivity.this)
                    .permissions(Permissions.STORAGE)
                    .result(new PermissionResult() {
                        @Override
                        public void onGranted() {
                            playService.updateMusicList();
                            startMusicActivity();
                            finish();
                        }

                        @Override
                        public void onDenied() {
                            ToastUtils.show(getString(R.string.no_permission, Permissions.STORAGE_DESC, "扫描本地歌曲"));
                            finish();
                            playService.stop();
                        }
                    })
                    .request();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    }

    private void initSplash() {
        File splashImg = new File(FileUtils.getSplashDir(this), "splash.jpg");
        if (splashImg.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(splashImg.getAbsolutePath());
            ivSplash.setImageBitmap(bitmap);
        }
    }

    private void updateSplash() {
        OkHttpUtils.get().url(Constants.SPLASH_URL).build()
                .execute(new JsonCallback<JSplash>(JSplash.class) {
                    @Override
                    public void onResponse(final JSplash response) {
                        if (response == null || TextUtils.isEmpty(response.getImg())) {
                            return;
                        }
                        String lastImgUrl = Preferences.getSplashUrl();
                        if (TextUtils.equals(lastImgUrl, response.getImg())) {
                            return;
                        }
                        OkHttpUtils.get().url(response.getImg()).build()
                                .execute(new FileCallBack(FileUtils.getSplashDir(AppCache.getContext()), "splash.jpg") {
                                    @Override
                                    public void inProgress(float progress, long total) {
                                    }

                                    @Override
                                    public void onResponse(File file) {
                                        Preferences.saveSplashUrl(response.getImg());
                                    }

                                    @Override
                                    public void onError(Call call, Exception e) {
                                    }
                                });
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                    }
                });
    }

    private void startMusicActivity() {
        Intent intent = new Intent();
        intent.setClass(this, MusicActivity.class);
        intent.putExtras(getIntent());
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onDestroy() {
        if (mPlayServiceConnection != null) {
            unbindService(mPlayServiceConnection);
        }
        super.onDestroy();
    }
}
