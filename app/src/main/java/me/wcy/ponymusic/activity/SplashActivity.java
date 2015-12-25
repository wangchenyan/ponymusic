package me.wcy.ponymusic.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import me.wcy.ponymusic.R;
import me.wcy.ponymusic.service.PlayService;
import me.wcy.ponymusic.utils.MusicUtils;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreen();
        setContentView(R.layout.activity_splash);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                startService();
                checkFile();
                startMusicActivity();
            }
        }, 500);
    }

    private void startService() {
        Intent intent = new Intent();
        intent.setClass(this, PlayService.class);
        startService(intent);
    }

    private void checkFile() {
        MusicUtils.getMusicDir();
        MusicUtils.getLrcDir();
    }

    private void setFullScreen() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }

    private void startMusicActivity() {
        Intent intent = new Intent();
        intent.setClass(this, MusicActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
    }
}
