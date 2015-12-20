package me.wcy.ponymusic.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import me.wcy.ponymusic.R;
import me.wcy.ponymusic.model.OnlineMusicListInfo;

/**
 * 歌单列表适配器
 * Created by wcy on 2015/12/19.
 */
public class OnlineMusicAdapter extends BaseAdapter {
    private Context mContext;
    private List<OnlineMusicListInfo> mData;

    public OnlineMusicAdapter(Context context, List<OnlineMusicListInfo> data) {
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.fragment_online_music_list_item, parent, false);
            holder = new ViewHolder();
            holder.ivIcon = (ImageView) convertView.findViewById(R.id.iv_icon);
            holder.tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.ivIcon.setImageResource(mData.get(position).getIcon());
        holder.tvTitle.setText(mData.get(position).getTitle());
        return convertView;
    }

    class ViewHolder {
        ImageView ivIcon;
        TextView tvTitle;
    }
}
