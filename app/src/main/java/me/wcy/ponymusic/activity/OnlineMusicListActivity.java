package me.wcy.ponymusic.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.squareup.okhttp.Request;
import com.zhy.http.okhttp.OkHttpUtils;

import me.wcy.ponymusic.R;
import me.wcy.ponymusic.model.OnlineMusic;
import me.wcy.ponymusic.model.OnlineMusicList;
import me.wcy.ponymusic.model.OnlineMusicListInfo;
import me.wcy.ponymusic.utils.Constants;
import me.wcy.ponymusic.utils.JsonCallback;

public class OnlineMusicListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_music_list);

        OnlineMusicListInfo info = (OnlineMusicListInfo) getIntent().getSerializableExtra(Constants.ONLINE_MUSIC_LIST_TYPE);

        OkHttpUtils.get().url(Constants.BASE_URL)
                .addParams("method", Constants.METHOD_GET_MUSIC_LIST)
                .addParams("type", info.getType())
                .addParams("size", "10")
                .addParams("offset", "0")
                .build()
                .execute(new JsonCallback<OnlineMusicList>(OnlineMusicList.class) {
                    @Override
                    public void onError(Request request, Exception e) {

                    }

                    @Override
                    public void onResponse(OnlineMusicList response) {
                        OnlineMusic[] sss = response.getSong_list();
                    }
                });
    }
}
