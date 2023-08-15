package me.wcy.music.activity

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.ListView
import androidx.appcompat.app.AlertDialog
import me.wcy.music.R
import me.wcy.music.adapter.OnMoreClickListener
import me.wcy.music.adapter.PlaylistAdapter
import me.wcy.music.model.Music
import me.wcy.music.service.AudioPlayer
import me.wcy.music.service.OnPlayerEventListener
import me.wcy.music.utils.binding.Bind

/**
 * 播放列表
 */
class PlaylistActivity : BaseActivity(), OnItemClickListener, OnMoreClickListener,
    OnPlayerEventListener {
    @Bind(R.id.lv_playlist)
    private val lvPlaylist: ListView? = null
    private var adapter: PlaylistAdapter? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playlist)
        adapter = PlaylistAdapter(AudioPlayer.get().getMusicList())
        adapter!!.setIsPlaylist(true)
        adapter!!.setOnMoreClickListener(this)
        lvPlaylist!!.adapter = adapter
        lvPlaylist.onItemClickListener = this
        AudioPlayer.get().addOnPlayEventListener(this)
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
        AudioPlayer.get().play(position)
    }

    override fun onMoreClick(position: Int) {
        val items = arrayOf("移除")
        val music: Music = AudioPlayer.get().getMusicList()!!.get(position)
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle(music.title)
        dialog.setItems(items) { dialog1: DialogInterface?, which: Int ->
            AudioPlayer.get().delete(position)
            adapter!!.notifyDataSetChanged()
        }
        dialog.show()
    }

    override fun onChange(music: Music?) {
        adapter!!.notifyDataSetChanged()
    }

    override fun onPlayerStart() {}
    override fun onPlayerPause() {}
    override fun onPublish(progress: Int) {}
    override fun onBufferingUpdate(percent: Int) {}
    override fun onDestroy() {
        AudioPlayer.get().removeOnPlayEventListener(this)
        super.onDestroy()
    }
}