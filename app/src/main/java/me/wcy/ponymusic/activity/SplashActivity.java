package me.wcy.ponymusic.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import me.wcy.ponymusic.R;
import me.wcy.ponymusic.utils.MusicUtils;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                init();
                Intent intent = new Intent();
                intent.setClass(SplashActivity.this, MusicActivity.class);
                SplashActivity.this.startActivity(intent);
                SplashActivity.this.finish();
            }
        }, 1000);
    }

    private void init() {
        MusicUtils.scanMusic(this);
    }
}
