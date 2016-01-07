package me.wcy.ponymusic.fragment;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.text.TextUtils;
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
import me.wcy.ponymusic.utils.Preferences;
import me.wcy.ponymusic.utils.ToastUtils;

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
    private DownloadReceiver mReceiver;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_local_music, container, false);
    }

    @Override
    protected void init() {
        mAdapter = new LocalMusicAdapter(getActivity());
        mReceiver = new DownloadReceiver();
        mAdapter.setOnMoreClickListener(this);
        lvLocalMusic.setAdapter(mAdapter);
        if (getPlayService().getPlayingMusic() != null && getPlayService().getPlayingMusic().getType() == MusicTypeEnum.LOACL) {
            lvLocalMusic.setSelection(getPlayService().getPlayingPosition());
        }
        updateView();

        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        getActivity().registerReceiver(mReceiver, filter);
    }

    @Override
    protected void setListener() {
        lvLocalMusic.setOnItemClickListener(this);
    }

    private void updateView() {
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
        final Music music = MusicUtils.getMusicList().get(position);
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle(music.getTitle());
        int itemsId = position == getPlayService().getPlayingPosition() ? R.array.local_music_dialog_no_delete : R.array.local_music_dialog;
        dialog.setItems(itemsId, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:// 分享
                        shareMusic(music);
                        break;
                    case 1:// 设为铃声
                        setRingtone(music);
                        break;
                    case 2:// 删除
                        deleteMusic(music);
                        break;
                }
            }
        });
        dialog.show();
    }

    public void onItemPlay() {
        updateView();
        if (getPlayService().getPlayingMusic().getType() == MusicTypeEnum.LOACL) {
            lvLocalMusic.smoothScrollToPosition(getPlayService().getPlayingPosition());
        }
    }

    /**
     * 分享音乐
     */
    private void shareMusic(Music music) {
        File file = new File(music.getUri());
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("audio/*");
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        startActivity(Intent.createChooser(intent, getString(R.string.share)));
    }

    /**
     * 设置铃声
     */
    private void setRingtone(Music music) {
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
            ToastUtils.show(R.string.setting_ringtone_success);
        }
        cursor.close();
    }

    /**
     * 删除音乐
     */
    private void deleteMusic(final Music music) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        String title = music.getTitle();
        String msg = getString(R.string.delete_music, title);
        dialog.setMessage(msg);
        dialog.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MusicUtils.getMusicList().remove(music);
                File file = new File(music.getUri());
                if (file.delete()) {
                    getPlayService().updatePlayingPosition();
                    updateView();
                    // 刷新媒体库
                    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + music.getUri()));
                    getActivity().sendBroadcast(intent);
                }
            }
        });
        dialog.setNegativeButton(R.string.cancel, null);
        dialog.show();
    }

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    class DownloadReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            String title = (String) Preferences.get(context, String.valueOf(id), "");
            if (TextUtils.isEmpty(title)) {
                return;
            }
            // 由于系统扫描音乐是异步执行，因此延迟刷新音乐列表
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!isAdded()) {
                        return;
                    }
                    getPlayService().updateMusicList();
                    updateView();
                }
            }, 500);
        }
    }
}
