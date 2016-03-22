package me.wcy.music.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import android.widget.ImageView;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import java.io.File;

import butterknife.Bind;
import me.wcy.music.R;
import me.wcy.music.application.MusicApplication;
import me.wcy.music.callback.JsonCallback;
import me.wcy.music.model.JSplash;
import me.wcy.music.service.PlayService;
import me.wcy.music.utils.Constants;
import me.wcy.music.utils.FileUtils;
import me.wcy.music.utils.Preferences;
import me.wcy.music.utils.SystemUtils;
import okhttp3.Call;

public class SplashActivity extends BaseActivity {
    @Bind(R.id.iv_splash)
    ImageView ivSplash;
    private ServiceConnection mPlayServiceConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        parseIntent();
    }

    @Override
    protected void setListener() {
    }

    @Override
    protected void onDestroy() {
        if (mPlayServiceConnection != null) {
            unbindService(mPlayServiceConnection);
        }
        super.onDestroy();
    }

    private void parseIntent() {
        if (SystemUtils.isStackResumed(this)) {
            startMusicActivity();
            finish();
            return;
        }

        startService();
        initSplash();
        updateSplash();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                bindService();
            }
        }, 1000);
    }

    private void startService() {
        Intent intent = new Intent();
        intent.setClass(this, PlayService.class);
        startService(intent);
    }

    private void bindService() {
        mPlayServiceConnection = new PlayServiceConnection();
        Intent intent = new Intent();
        intent.setClass(this, PlayService.class);
        bindService(intent, mPlayServiceConnection, Context.BIND_AUTO_CREATE);
    }

    class PlayServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            PlayService playService = ((PlayService.PlayBinder) service).getService();
            playService.updateMusicList();
            startMusicActivity();
            finish();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    }

    private void initSplash() {
        File splashImg = new File(FileUtils.getSplashDir(this), "splash.jpg");
        if (!splashImg.exists()) {
            return;
        }
        Bitmap bitmap = BitmapFactory.decodeFile(splashImg.getAbsolutePath());
        ivSplash.setImageBitmap(bitmap);
    }

    private static void updateSplash() {
        OkHttpUtils.get().url(Constants.SPLASH_URL).build()
                .execute(new JsonCallback<JSplash>(JSplash.class) {
                    @Override
                    public void onResponse(JSplash response) {
                        if (response == null || TextUtils.isEmpty(response.getImg())) {
                            return;
                        }
                        String lastImgUrl = Preferences.getSplashUrl("");
                        if (lastImgUrl.equals(response.getImg())) {
                            return;
                        }
                        Preferences.saveSplashUrl(response.getImg());
                        OkHttpUtils.get().url(response.getImg()).build()
                                .execute(new FileCallBack(FileUtils.getSplashDir(MusicApplication.getInstance().getApplicationContext()), "splash.jpg") {
                                    @Override
                                    public void inProgress(float progress) {
                                    }

                                    @Override
                                    public void onResponse(File response) {
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
}
