package me.wcy.ponymusic.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import me.wcy.ponymusic.R;
import me.wcy.ponymusic.activity.MusicActivity;
import me.wcy.ponymusic.model.OnlineMusic;

/**
 * 歌单适配器
 * Created by wcy on 2015/12/22.
 */
public class OnlineMusicListAdapter extends BaseAdapter {
    private Context mContext;
    private List<OnlineMusic> mData;
    private OnMoreClickListener mListener;
    private DisplayImageOptions mOptions;
    private int mPlayingPosition = -1;

    public OnlineMusicListAdapter(Context context, List<OnlineMusic> data) {
        this.mContext = context;
        this.mData = data;
        mOptions = new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.ic_music_list_default_cover)
                .showImageForEmptyUri(R.drawable.ic_music_list_default_cover)
                .showImageOnFail(R.drawable.ic_music_list_default_cover)
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .build();
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.fragment_local_music_list_item, parent, false);
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
        OnlineMusic onlineMusic = mData.get(position);
        ImageLoader.getInstance().displayImage(onlineMusic.getPic_small(), holder.ivCover, mOptions);
        holder.tvTitle.setText(onlineMusic.getTitle());
        String artist = onlineMusic.getArtist_name() + " - " + onlineMusic.getAlbum_title();
        holder.tvArtist.setText(artist);
        holder.ivMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onMoreClick(position);
            }
        });
        return convertView;
    }

    public void updatePlayingPosition() {
        mPlayingPosition = ((MusicActivity) mContext).getPlayService().getPlayingPosition();
    }

    public void setOnMoreClickListener(OnMoreClickListener listener) {
        mListener = listener;
    }

    class ViewHolder {
        ImageView ivPlaying;
        ImageView ivCover;
        TextView tvTitle;
        TextView tvArtist;
        ImageView ivMore;
    }
}
