package me.wcy.music.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.wcy.music.R;
import me.wcy.music.activity.MusicActivity;
import me.wcy.music.enums.MusicTypeEnum;
import me.wcy.music.model.Music;
import me.wcy.music.service.PlayService;
import me.wcy.music.utils.CoverLoader;
import me.wcy.music.utils.MusicUtils;
import me.wcy.music.utils.Utils;

/**
 * 本地音乐列表适配器
 * Created by wcy on 2015/11/27.
 */
public class LocalMusicAdapter extends BaseAdapter {
    private Context mContext;
    private OnMoreClickListener mListener;
    private int mPlayingPosition;

    public LocalMusicAdapter(Context context) {
        mContext = context;
    }

    @Override
    public int getCount() {
        return MusicUtils.getMusicList().size();
    }

    @Override
    public Object getItem(int position) {
        return MusicUtils.getMusicList().get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.view_holder_music, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (position == mPlayingPosition) {
            holder.ivPlaying.setVisibility(View.VISIBLE);
        } else {
            holder.ivPlaying.setVisibility(View.INVISIBLE);
        }
        final Music music = MusicUtils.getMusicList().get(position);
        Bitmap cover = CoverLoader.getInstance().loadThumbnail(music.getCoverUri());
        holder.ivCover.setImageBitmap(cover);
        holder.tvTitle.setText(music.getTitle());
        String artist = Utils.getArtistAndAlbum(music.getArtist(), music.getAlbum());
        holder.tvArtist.setText(artist);
        holder.ivMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onMoreClick(position);
                }
            }
        });
        return convertView;
    }

    public void updatePlayingPosition() {
        PlayService playService = ((MusicActivity) mContext).getPlayService();
        if (playService.getPlayingMusic() != null && playService.getPlayingMusic().getType() == MusicTypeEnum.LOCAL) {
            mPlayingPosition = playService.getPlayingPosition();
        } else {
            mPlayingPosition = -1;
        }
    }

    public void setOnMoreClickListener(OnMoreClickListener listener) {
        mListener = listener;
    }

    class ViewHolder {
        @Bind(R.id.iv_playing)
        ImageView ivPlaying;
        @Bind(R.id.iv_cover)
        ImageView ivCover;
        @Bind(R.id.tv_title)
        TextView tvTitle;
        @Bind(R.id.tv_artist)
        TextView tvArtist;
        @Bind(R.id.iv_more)
        ImageView ivMore;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
