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
        return MusicUtils.sMusicList.size();
    }

    @Override
    public Object getItem(int position) {
        return MusicUtils.sMusicList.get(position);
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
        Bitmap cover = CoverLoader.getInstance().loadThumbnail(MusicUtils.sMusicList.get(position).getCoverUri());
        if (cover != null) {
            holder.ivCover.setImageBitmap(cover);
        } else {
            holder.ivCover.setImageResource(R.drawable.ic_default_cover);
        }
        holder.tvTitle.setText(MusicUtils.sMusicList.get(position).getTitle());
        String artist = MusicUtils.sMusicList.get(position).getArtist() + " - " + MusicUtils.sMusicList.get(position).getAlbum();
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
