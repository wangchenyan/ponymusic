package me.wcy.ponymusic.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import me.wcy.ponymusic.R;
import me.wcy.ponymusic.model.MusicInfo;
import me.wcy.ponymusic.utils.CoverLoader;
import me.wcy.ponymusic.utils.MusicUtils;

/**
 * Created by wcy on 2015/11/27.
 */
public class LocalMusicAdapter extends BaseAdapter {
    private Context mContext;
    private int mPlayingPosition;

    public LocalMusicAdapter(Context context, int playingPosition) {
        mContext = context;
        mPlayingPosition = playingPosition;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.fragment_local_music_list_item, null);
            holder = new ViewHolder();
            holder.ivPlaying = (ImageView) convertView.findViewById(R.id.iv_playing);
            holder.ivCover = (ImageView) convertView.findViewById(R.id.iv_cover);
            holder.tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
            holder.tvArtist = (TextView) convertView.findViewById(R.id.tv_artist);
            holder.ivMore = (ImageView) convertView.findViewById(R.id.iv_more);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (position == mPlayingPosition) {
            holder.ivPlaying.setVisibility(View.VISIBLE);
        } else {
            holder.ivPlaying.setVisibility(View.INVISIBLE);
        }
        MusicInfo musicInfo = MusicUtils.getMusicList().get(position);
        Bitmap cover = CoverLoader.getInstance().loadThumbnail(musicInfo.getCoverUri());
        holder.ivCover.setImageBitmap(cover);
        holder.tvTitle.setText(musicInfo.getTitle());
        String artist = musicInfo.getArtist() + " - " + musicInfo.getAlbum();
        holder.tvArtist.setText(artist);
        return convertView;
    }

    public void setPlayingPosition(int position) {
        mPlayingPosition = position;
    }

    class ViewHolder {
        ImageView ivPlaying;
        ImageView ivCover;
        TextView tvTitle;
        TextView tvArtist;
        ImageView ivMore;
    }

}
