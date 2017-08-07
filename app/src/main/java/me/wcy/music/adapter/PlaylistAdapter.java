package me.wcy.music.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import me.wcy.music.R;
import me.wcy.music.http.HttpCallback;
import me.wcy.music.http.HttpClient;
import me.wcy.music.model.OnlineMusic;
import me.wcy.music.model.OnlineMusicList;
import me.wcy.music.model.SongListInfo;
import me.wcy.music.utils.binding.Bind;
import me.wcy.music.utils.binding.ViewBinder;

/**
 * 歌单列表适配器
 * Created by wcy on 2015/12/19.
 */
public class PlaylistAdapter extends BaseAdapter {
    private static final int TYPE_PROFILE = 0;
    private static final int TYPE_MUSIC_LIST = 1;
    private Context mContext;
    private List<SongListInfo> mData;

    public PlaylistAdapter(List<SongListInfo> data) {
        mData = data;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean isEnabled(int position) {
        return getItemViewType(position) == TYPE_MUSIC_LIST;
    }

    @Override
    public int getItemViewType(int position) {
        if (mData.get(position).getType().equals("#")) {
            return TYPE_PROFILE;
        } else {
            return TYPE_MUSIC_LIST;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        mContext = parent.getContext();
        ViewHolderProfile holderProfile;
        ViewHolderMusicList holderMusicList;
        SongListInfo songListInfo = mData.get(position);
        int itemViewType = getItemViewType(position);
        switch (itemViewType) {
            case TYPE_PROFILE:
                if (convertView == null) {
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.view_holder_playlist_profile, parent, false);
                    holderProfile = new ViewHolderProfile(convertView);
                    convertView.setTag(holderProfile);
                } else {
                    holderProfile = (ViewHolderProfile) convertView.getTag();
                }
                holderProfile.tvProfile.setText(songListInfo.getTitle());
                break;
            case TYPE_MUSIC_LIST:
                if (convertView == null) {
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.view_holder_playlist, parent, false);
                    holderMusicList = new ViewHolderMusicList(convertView);
                    convertView.setTag(holderMusicList);
                } else {
                    holderMusicList = (ViewHolderMusicList) convertView.getTag();
                }
                getMusicListInfo(songListInfo, holderMusicList);
                holderMusicList.vDivider.setVisibility(isShowDivider(position) ? View.VISIBLE : View.GONE);
                break;
        }
        return convertView;
    }

    private boolean isShowDivider(int position) {
        return position != mData.size() - 1;
    }

    private void getMusicListInfo(final SongListInfo songListInfo, final ViewHolderMusicList holderMusicList) {
        if (songListInfo.getCoverUrl() == null) {
            holderMusicList.tvMusic1.setTag(songListInfo.getTitle());
            holderMusicList.ivCover.setImageResource(R.drawable.default_cover);
            holderMusicList.tvMusic1.setText("1.加载中…");
            holderMusicList.tvMusic2.setText("2.加载中…");
            holderMusicList.tvMusic3.setText("3.加载中…");
            HttpClient.getSongListInfo(songListInfo.getType(), 3, 0, new HttpCallback<OnlineMusicList>() {
                @Override
                public void onSuccess(OnlineMusicList response) {
                    if (response == null || response.getSong_list() == null) {
                        return;
                    }
                    if (!songListInfo.getTitle().equals(holderMusicList.tvMusic1.getTag())) {
                        return;
                    }
                    parse(response, songListInfo);
                    setData(songListInfo, holderMusicList);
                }

                @Override
                public void onFail(Exception e) {
                }
            });
        } else {
            holderMusicList.tvMusic1.setTag(null);
            setData(songListInfo, holderMusicList);
        }
    }

    private void parse(OnlineMusicList response, SongListInfo songListInfo) {
        List<OnlineMusic> onlineMusics = response.getSong_list();
        songListInfo.setCoverUrl(response.getBillboard().getPic_s260());
        if (onlineMusics.size() >= 1) {
            songListInfo.setMusic1(mContext.getString(R.string.song_list_item_title_1,
                    onlineMusics.get(0).getTitle(), onlineMusics.get(0).getArtist_name()));
        } else {
            songListInfo.setMusic1("");
        }
        if (onlineMusics.size() >= 2) {
            songListInfo.setMusic2(mContext.getString(R.string.song_list_item_title_2,
                    onlineMusics.get(1).getTitle(), onlineMusics.get(1).getArtist_name()));
        } else {
            songListInfo.setMusic2("");
        }
        if (onlineMusics.size() >= 3) {
            songListInfo.setMusic3(mContext.getString(R.string.song_list_item_title_3,
                    onlineMusics.get(2).getTitle(), onlineMusics.get(2).getArtist_name()));
        } else {
            songListInfo.setMusic3("");
        }
    }

    private void setData(SongListInfo songListInfo, ViewHolderMusicList holderMusicList) {
        holderMusicList.tvMusic1.setText(songListInfo.getMusic1());
        holderMusicList.tvMusic2.setText(songListInfo.getMusic2());
        holderMusicList.tvMusic3.setText(songListInfo.getMusic3());
        Glide.with(mContext)
                .load(songListInfo.getCoverUrl())
                .placeholder(R.drawable.default_cover)
                .error(R.drawable.default_cover)
                .into(holderMusicList.ivCover);
    }

    private static class ViewHolderProfile {
        @Bind(R.id.tv_profile)
        private TextView tvProfile;

        public ViewHolderProfile(View view) {
            ViewBinder.bind(this, view);
        }
    }

    private static class ViewHolderMusicList {
        @Bind(R.id.iv_cover)
        private ImageView ivCover;
        @Bind(R.id.tv_music_1)
        private TextView tvMusic1;
        @Bind(R.id.tv_music_2)
        private TextView tvMusic2;
        @Bind(R.id.tv_music_3)
        private TextView tvMusic3;
        @Bind(R.id.v_divider)
        private View vDivider;

        public ViewHolderMusicList(View view) {
            ViewBinder.bind(this, view);
        }
    }
}
