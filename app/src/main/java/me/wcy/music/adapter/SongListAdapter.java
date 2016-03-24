package me.wcy.music.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.wcy.music.R;
import me.wcy.music.callback.JsonCallback;
import me.wcy.music.model.JOnlineMusic;
import me.wcy.music.model.JOnlineMusicList;
import me.wcy.music.model.SongListInfo;
import me.wcy.music.utils.Constants;
import me.wcy.music.utils.ImageUtils;
import okhttp3.Call;

/**
 * 歌单列表适配器
 * Created by wcy on 2015/12/19.
 */
public class SongListAdapter extends BaseAdapter {
    private static final int TYPE_PROFILE = 0;
    private static final int TYPE_MUSIC_LIST = 1;
    private Context mContext;
    private List<SongListInfo> mData;

    public SongListAdapter(Context context, List<SongListInfo> data) {
        mContext = context;
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
        if (mData.get(position).getType().equals("*")) {
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
        SongListInfo songListInfo = mData.get(position);
        int itemViewType = getItemViewType(position);
        switch (itemViewType) {
            case TYPE_PROFILE:
                convertView = LayoutInflater.from(mContext).inflate(R.layout.view_holder_fragment_song_list_profile, parent, false);
                ViewHolderProfile holderProfile = new ViewHolderProfile(convertView);
                convertView.setTag(holderProfile);
                holderProfile.tvProfile.setText(songListInfo.getTitle());
                break;
            case TYPE_MUSIC_LIST:
                convertView = LayoutInflater.from(mContext).inflate(R.layout.view_holder_fragment_song_list, parent, false);
                ViewHolderMusicList holderMusicList = new ViewHolderMusicList(convertView);
                convertView.setTag(holderMusicList);
                getMusicListInfo(songListInfo, holderMusicList);
                break;
        }
        return convertView;
    }

    private void getMusicListInfo(final SongListInfo songListInfo, final ViewHolderMusicList holderMusicList) {
        if (songListInfo.getCoverUrl() == null) {
            OkHttpUtils.get().url(Constants.BASE_URL)
                    .addParams(Constants.PARAM_METHOD, Constants.METHOD_GET_MUSIC_LIST)
                    .addParams(Constants.PARAM_TYPE, songListInfo.getType())
                    .addParams(Constants.PARAM_SIZE, "3")
                    .build()
                    .execute(new JsonCallback<JOnlineMusicList>(JOnlineMusicList.class) {
                        @Override
                        public void onResponse(JOnlineMusicList response) {
                            if (response == null || response.getSong_list() == null) {
                                return;
                            }
                            JOnlineMusic[] jOnlineMusics = response.getSong_list();
                            songListInfo.setCoverUrl(response.getBillboard().getPic_s260());
                            if (jOnlineMusics.length >= 1) {
                                songListInfo.setMusic1("1." + jOnlineMusics[0].getTitle() + " - " + jOnlineMusics[0].getArtist_name());
                            } else {
                                songListInfo.setMusic1("");
                            }
                            if (jOnlineMusics.length >= 2) {
                                songListInfo.setMusic2("2." + jOnlineMusics[1].getTitle() + " - " + jOnlineMusics[1].getArtist_name());
                            } else {
                                songListInfo.setMusic2("");
                            }
                            if (jOnlineMusics.length >= 3) {
                                songListInfo.setMusic3("3." + jOnlineMusics[2].getTitle() + " - " + jOnlineMusics[2].getArtist_name());
                            } else {
                                songListInfo.setMusic3("");
                            }
                            ImageLoader.getInstance().displayImage(songListInfo.getCoverUrl(), holderMusicList.ivCover, ImageUtils.getDefaultDisplayImageOptions());
                            holderMusicList.tvMusic1.setText(songListInfo.getMusic1());
                            holderMusicList.tvMusic2.setText(songListInfo.getMusic2());
                            holderMusicList.tvMusic3.setText(songListInfo.getMusic3());
                        }

                        @Override
                        public void onError(Call call, Exception e) {
                        }
                    });
        } else {
            ImageLoader.getInstance().displayImage(songListInfo.getCoverUrl(), holderMusicList.ivCover, ImageUtils.getDefaultDisplayImageOptions());
            holderMusicList.tvMusic1.setText(songListInfo.getMusic1());
            holderMusicList.tvMusic2.setText(songListInfo.getMusic2());
            holderMusicList.tvMusic3.setText(songListInfo.getMusic3());
        }
    }

    class ViewHolderProfile {
        @Bind(R.id.tv_profile)
        TextView tvProfile;

        public ViewHolderProfile(View view) {
            ButterKnife.bind(this, view);
        }
    }

    class ViewHolderMusicList {
        @Bind(R.id.iv_cover)
        ImageView ivCover;
        @Bind(R.id.tv_music_1)
        TextView tvMusic1;
        @Bind(R.id.tv_music_2)
        TextView tvMusic2;
        @Bind(R.id.tv_music_3)
        TextView tvMusic3;

        public ViewHolderMusicList(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
