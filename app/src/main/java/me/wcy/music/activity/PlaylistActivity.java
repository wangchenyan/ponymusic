package me.wcy.music.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import me.wcy.music.R;
import me.wcy.music.adapter.OnMoreClickListener;
import me.wcy.music.adapter.PlaylistAdapter;
import me.wcy.music.model.Music;
import me.wcy.music.service.AudioPlayer;
import me.wcy.music.utils.binding.Bind;

/**
 * 播放列表
 */
public class PlaylistActivity extends BaseActivity implements AdapterView.OnItemClickListener, OnMoreClickListener {
    @Bind(R.id.lv_playlist)
    private ListView lvPlaylist;

    private PlaylistAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);
    }

    @Override
    protected void onServiceBound() {
        mAdapter = new PlaylistAdapter(AudioPlayer.get().getMusicList());
        mAdapter.setOnMoreClickListener(this);
        lvPlaylist.setAdapter(mAdapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        AudioPlayer.get().play(position);
    }

    @Override
    public void onMoreClick(int position) {
        String[] items = new String[]{"移除"};
        Music music = AudioPlayer.get().getMusicList().get(position);
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(music.getTitle());
        dialog.setItems(items, (dialog1, which) -> {
            if (AudioPlayer.get().getPlayPosition() == position) {
                AudioPlayer.get().stopPlayer();
            }
            AudioPlayer.get().getMusicList().remove(position);
            mAdapter.notifyDataSetChanged();
            AudioPlayer.get().next();
        });
        dialog.show();
    }
}
