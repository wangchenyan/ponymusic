package me.wcy.ponymusic.fragment;

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;

import butterknife.Bind;
import me.wcy.ponymusic.R;
import me.wcy.ponymusic.adapter.LocalMusicAdapter;
import me.wcy.ponymusic.adapter.OnMoreClickListener;
import me.wcy.ponymusic.enums.MusicTypeEnum;
import me.wcy.ponymusic.model.Music;
import me.wcy.ponymusic.utils.MusicUtils;
import me.wcy.ponymusic.utils.ToastUtil;

/**
 * 本地音乐列表
 * Created by wcy on 2015/11/26.
 */
public class LocalMusicFragment extends BaseFragment implements AdapterView.OnItemClickListener, OnMoreClickListener {
    @Bind(R.id.lv_local_music)
    ListView lvLocalMusic;
    @Bind(R.id.tv_empty)
    TextView tvEmpty;
    private LocalMusicAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_local_music, container, false);
    }

    @Override
    protected void init() {
        mAdapter = new LocalMusicAdapter(getActivity());
        mAdapter.setOnMoreClickListener(this);
        lvLocalMusic.setAdapter(mAdapter);
        if (getPlayService().getPlayingMusic() != null && getPlayService().getPlayingMusic().getType() == MusicTypeEnum.LOACL) {
            lvLocalMusic.setSelection(getPlayService().getPlayingPosition());
        }
    }

    @Override
    protected void setListener() {
        lvLocalMusic.setOnItemClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (MusicUtils.getMusicList().isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
        } else {
            tvEmpty.setVisibility(View.GONE);
        }
        mAdapter.updatePlayingPosition();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        getPlayService().play(position);
    }

    @Override
    public void onMoreClick(final int position) {
        Music music = MusicUtils.getMusicList().get(position);
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle(music.getTitle());
        int itemsId = position == getPlayService().getPlayingPosition() ? R.array.local_music_list_dialog_no_delete : R.array.local_music_list_dialog;
        dialog.setItems(itemsId, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        shareMusic(position);
                        break;
                    case 1:
                        setRingtone(position);
                        break;
                    case 2:
                        deleteMusic(position);
                        break;
                }
            }
        });
        dialog.show();
    }

    public void onItemPlay() {
        mAdapter.updatePlayingPosition();
        mAdapter.notifyDataSetChanged();
        if (getPlayService().getPlayingMusic().getType() == MusicTypeEnum.LOACL) {
            lvLocalMusic.smoothScrollToPosition(getPlayService().getPlayingPosition());
        }
    }

    /**
     * 分享音乐
     */
    private void shareMusic(int position) {
        Music music = MusicUtils.getMusicList().get(position);
        File file = new File(music.getUri());
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("audio/*");
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        startActivity(Intent.createChooser(intent, getString(R.string.share)));
    }

    /**
     * 设置铃声
     */
    private void setRingtone(int position) {
        Music music = MusicUtils.getMusicList().get(position);
        Uri uri = MediaStore.Audio.Media.getContentUriForPath(music.getUri());
        // 查询音乐文件在媒体库是否存在
        Cursor cursor = getActivity().getContentResolver().query(uri, null,
                MediaStore.MediaColumns.DATA + "=?", new String[]{music.getUri()}, null);
        if (cursor == null) {
            return;
        }
        if (cursor.moveToFirst() && cursor.getCount() > 0) {
            String _id = cursor.getString(0);
            ContentValues values = new ContentValues();
            values.put(MediaStore.Audio.Media.IS_RINGTONE, true);
            values.put(MediaStore.Audio.Media.IS_NOTIFICATION, false);
            values.put(MediaStore.Audio.Media.IS_ALARM, false);
            values.put(MediaStore.Audio.Media.IS_MUSIC, false);

            getActivity().getContentResolver().update(uri, values, MediaStore.MediaColumns.DATA + "=?", new String[]{music.getUri()});
            Uri newUri = ContentUris.withAppendedId(uri, Long.valueOf(_id));
            RingtoneManager.setActualDefaultRingtoneUri(getActivity(), RingtoneManager.TYPE_RINGTONE, newUri);
            ToastUtil.show(R.string.setting_ringtone_success);
        }
        cursor.close();
    }

    /**
     * 删除音乐
     */
    private void deleteMusic(final int position) {
        final Music playingMusic = MusicUtils.getMusicList().get(getPlayService().getPlayingPosition());
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        String title = MusicUtils.getMusicList().get(position).getTitle();
        String msg = getString(R.string.delete_music);
        msg = String.format(msg, title);
        dialog.setMessage(msg);
        dialog.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Music music = MusicUtils.getMusicList().remove(position);
                File file = new File(music.getUri());
                if (file.delete()) {
                    getPlayService().updatePlayingPosition(playingMusic);
                    onResume();
                    // 刷新媒体库
                    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + music.getUri()));
                    getActivity().sendBroadcast(intent);
                }
            }
        });
        dialog.setNegativeButton(R.string.cancel, null);
        dialog.show();
    }
}
