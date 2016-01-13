package me.wcy.music.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import me.wcy.music.R;
import me.wcy.music.model.JSearchMusic;

/**
 * 搜索结果适配器
 * Created by hzwangchenyan on 2016/1/13.
 */
public class SearchMusicAdapter extends BaseAdapter {
    private Context mContext;
    private List<JSearchMusic.JSong> mData;
    private OnMoreClickListener mListener;

    public SearchMusicAdapter(Context context, List<JSearchMusic.JSong> data) {
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.music_list_item, parent, false);
            holder = new ViewHolder();
            convertView.findViewById(R.id.iv_cover).setVisibility(View.GONE);
            holder.tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
            holder.tvArtist = (TextView) convertView.findViewById(R.id.tv_artist);
            holder.ivMore = (ImageView) convertView.findViewById(R.id.iv_more);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tvTitle.setText(mData.get(position).getSongname());
        holder.tvArtist.setText(mData.get(position).getArtistname());
        holder.ivMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onMoreClick(position);
            }
        });
        return convertView;
    }

    public void setOnMoreClickListener(OnMoreClickListener listener) {
        mListener = listener;
    }

    class ViewHolder {
        TextView tvTitle;
        TextView tvArtist;
        ImageView ivMore;
    }
}
