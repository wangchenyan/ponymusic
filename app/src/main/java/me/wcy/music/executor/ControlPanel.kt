package me.wcy.music.executor

import android.content.Intent
import android.graphics.Bitmap
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import me.wcy.music.R
import me.wcy.music.model.Music
import me.wcy.music.playing.PlaylistActivity
import me.wcy.music.service.OnPlayerEventListener
import me.wcy.music.utils.CoverLoader
import me.wcy.music.utils.binding.Bind
import me.wcy.music.utils.binding.ViewBinder

/**
 * Created by hzwangchenyan on 2018/1/26.
 */
class ControlPanel(view: View?) : View.OnClickListener, OnPlayerEventListener {
    @Bind(R.id.iv_play_bar_cover)
    lateinit var ivPlayBarCover: ImageView

    @Bind(R.id.tv_play_bar_title)
    lateinit var tvPlayBarTitle: TextView

    @Bind(R.id.tv_play_bar_artist)
    lateinit var tvPlayBarArtist: TextView

    @Bind(R.id.iv_play_bar_play)
    lateinit var ivPlayBarPlay: ImageView

    @Bind(R.id.iv_play_bar_next)
    lateinit var ivPlayBarNext: ImageView

    @Bind(R.id.v_play_bar_playlist)
    lateinit var vPlayBarPlaylist: ImageView

    @Bind(R.id.pb_play_bar)
    lateinit var mProgressBar: ProgressBar

    init {
        ViewBinder.bind(this, view)
        ivPlayBarPlay!!.setOnClickListener(this)
        ivPlayBarNext!!.setOnClickListener(this)
        vPlayBarPlaylist!!.setOnClickListener(this)
        // onChange(AudioPlayer.get().playMusic)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.iv_play_bar_play -> {}//AudioPlayer.get().playPause()
            R.id.iv_play_bar_next -> {}//AudioPlayer.get().next()
            R.id.v_play_bar_playlist -> {
                val context = vPlayBarPlaylist!!.context
                val intent = Intent(context, PlaylistActivity::class.java)
                context.startActivity(intent)
            }
        }
    }

    override fun onChange(music: Music?) {
        if (music == null) {
            return
        }
        val cover: Bitmap? = CoverLoader.get().loadThumb(music)
        ivPlayBarCover.setImageBitmap(cover)
        tvPlayBarTitle.setText(music.title)
        tvPlayBarArtist.setText(music.artist)
//        ivPlayBarPlay.isSelected =
//            AudioPlayer.get().isPlaying || AudioPlayer.get().isPreparing
        mProgressBar.max = music.duration.toInt()
//        mProgressBar.progress = AudioPlayer.get().audioPosition.toInt()
    }

    override fun onPlayerStart() {
        ivPlayBarPlay.isSelected = true
    }

    override fun onPlayerPause() {
        ivPlayBarPlay.isSelected = false
    }

    override fun onPublish(progress: Int) {
        mProgressBar.progress = progress
    }

    override fun onBufferingUpdate(percent: Int) {}
}