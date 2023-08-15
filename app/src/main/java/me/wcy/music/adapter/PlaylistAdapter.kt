package me.wcy.music.adapter

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import me.wcy.music.R
import me.wcy.music.model.Music
import me.wcy.music.service.AudioPlayer
import me.wcy.music.utils.CoverLoader
import me.wcy.music.utils.FileUtils
import me.wcy.music.utils.binding.Bind
import me.wcy.music.utils.binding.ViewBinder

/**
 * 本地音乐列表适配器
 * Created by wcy on 2015/11/27.
 */
class PlaylistAdapter(private val musicList: List<Music>?) : BaseAdapter() {
    private var listener: OnMoreClickListener? = null
    private var isPlaylist = false
    fun setIsPlaylist(isPlaylist: Boolean) {
        this.isPlaylist = isPlaylist
    }

    fun setOnMoreClickListener(listener: OnMoreClickListener?) {
        this.listener = listener
    }

    override fun getCount(): Int {
        return musicList!!.size
    }

    override fun getItem(position: Int): Any {
        return musicList!![position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView2 = convertView
        val holder: ViewHolder
        if (convertView2 == null) {
            convertView2 = LayoutInflater.from(parent.context)
                .inflate(R.layout.view_holder_music, parent, false)
            holder = ViewHolder(convertView2)
            convertView2.tag = holder
        } else {
            holder = convertView2.tag as ViewHolder
        }
        holder.vPlaying!!.visibility = if (isPlaylist && position == AudioPlayer.get().playPosition
        ) View.VISIBLE else View.INVISIBLE
        val music = musicList!![position]
        val cover: Bitmap? = CoverLoader.get().loadThumb(music)
        holder.ivCover!!.setImageBitmap(cover)
        holder.tvTitle.setText(music.title)
        val artist = FileUtils.getArtistAndAlbum(music.artist, music.album)
        holder.tvArtist!!.text = artist
        holder.ivMore!!.setOnClickListener { v: View? ->
            if (listener != null) {
                listener!!.onMoreClick(position)
            }
        }
        holder.vDivider!!.visibility = if (isShowDivider(position)) View.VISIBLE else View.GONE
        return convertView2!!
    }

    private fun isShowDivider(position: Int): Boolean {
        return position != musicList!!.size - 1
    }

    private class ViewHolder(view: View?) {
        @Bind(R.id.v_playing)
        lateinit var vPlaying: View

        @Bind(R.id.iv_cover)
        lateinit var  ivCover: ImageView

        @Bind(R.id.tv_title)
        lateinit var  tvTitle: TextView

        @Bind(R.id.tv_artist)
        lateinit var  tvArtist: TextView

        @Bind(R.id.iv_more)
        lateinit var  ivMore: ImageView

        @Bind(R.id.v_divider)
        lateinit var  vDivider: View

        init {
            ViewBinder.bind(this, view)
        }
    }
}