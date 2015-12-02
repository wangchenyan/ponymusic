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
import android.widget.Toast;

import java.io.File;

import butterknife.Bind;
import me.wcy.ponymusic.R;
import me.wcy.ponymusic.adapter.LocalMusicAdapter;
import me.wcy.ponymusic.model.MusicInfo;
import me.wcy.ponymusic.utils.MusicUtils;

/**
 * 本地音乐列表
 * Created by wcy on 2015/11/26.
 */
public class LocalMusicFragment extends BaseFragment implements AdapterView.OnItemClickListener, LocalMusicAdapter.OnMoreClickListener {
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
        if (MusicUtils.getMusicList().isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            return;
        }
        int playingPosition = getPlayService().getPlayingPosition();
        mAdapter = new LocalMusicAdapter(getActivity());
        mAdapter.updatePlayingPosition();
        mAdapter.setOnMoreClickListener(this);
        lvLocalMusic.setAdapter(mAdapter);
        lvLocalMusic.setSelection(playingPosition);
    }

    @Override
    protected void setListener() {
        lvLocalMusic.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        getPlayService().play(position);
    }

    @Override
    public void onMoreClick(final int position) {
        MusicInfo musicInfo = MusicUtils.getMusicList().get(position);
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle(musicInfo.getTitle());
        int itemsId = position == getPlayService().getPlayingPosition() ? R.array.music_list_dialog_no_delete : R.array.music_list_dialog;
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

    public void onItemPlay(int position) {
        mAdapter.updatePlayingPosition();
        mAdapter.notifyDataSetChanged();
        lvLocalMusic.smoothScrollToPosition(position);
    }

    /**
     * 分享音乐
     */
    private void shareMusic(int position) {
        MusicInfo musicInfo = MusicUtils.getMusicList().get(position);
        File file = new File(musicInfo.getUri());
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("audio/*");
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        startActivity(Intent.createChooser(intent, getString(R.string.share)));
    }

    /**
     * 设置铃声
     */
    private void setRingtone(int position) {
        MusicInfo musicInfo = MusicUtils.getMusicList().get(position);
        Uri uri = MediaStore.Audio.Media.getContentUriForPath(musicInfo.getUri());
        // 查询音乐文件在媒体库是否存在
        Cursor cursor = getActivity().getContentResolver().query(uri, null,
                MediaStore.MediaColumns.DATA + "=?", new String[]{musicInfo.getUri()}, null);
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

            getActivity().getContentResolver().update(uri, values, MediaStore.MediaColumns.DATA + "=?", new String[]{musicInfo.getUri()});
            Uri newUri = ContentUris.withAppendedId(uri, Long.valueOf(_id));
            RingtoneManager.setActualDefaultRingtoneUri(getActivity(), RingtoneManager.TYPE_RINGTONE, newUri);
            Toast.makeText(getActivity(), R.string.setting_ringtone_success, Toast.LENGTH_SHORT).show();
        }
        cursor.close();
    }

    /**
     * 删除音乐
     */
    private void deleteMusic(final int position) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        String title = MusicUtils.getMusicList().get(position).getTitle();
        String msg = getString(R.string.delete_music);
        msg = String.format(msg, title);
        dialog.setMessage(msg);
        dialog.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MusicInfo musicInfo = MusicUtils.getMusicList().remove(position);
                File file = new File(musicInfo.getUri());
                if (file.delete()) {
                    if (position < getPlayService().getPlayingPosition()) {
                        getPlayService().setPlayingPosition(getPlayService().getPlayingPosition() - 1);
                        mAdapter.updatePlayingPosition();
                    }
                    mAdapter.notifyDataSetChanged();
                    //刷新媒体库
                    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + musicInfo.getUri()));
                    getActivity().sendBroadcast(intent);
                }
            }
        });
        dialog.setNegativeButton(R.string.cancel, null);
        dialog.show();
    }
}
