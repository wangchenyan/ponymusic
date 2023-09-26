package me.wcy.music.main.playlist

import android.annotation.SuppressLint
import me.wcy.music.databinding.ItemCurrentPlaylistBinding
import me.wcy.music.service.AudioPlayer
import me.wcy.music.storage.db.entity.SongEntity
import me.wcy.radapter3.RItemBinder

/**
 * Created by wangchenyan.top on 2023/9/4.
 */
class CurrentPlaylistItemBinder(private val audioPlayer: AudioPlayer) :
    RItemBinder<ItemCurrentPlaylistBinding, SongEntity>() {
    @SuppressLint("SetTextI18n")
    override fun onBind(viewBinding: ItemCurrentPlaylistBinding, item: SongEntity, position: Int) {
        viewBinding.root.isSelected = (audioPlayer.currentSong.value == item)
        viewBinding.root.setOnClickListener {
            audioPlayer.play(item)
        }
        viewBinding.tvTitle.text = item.title
        viewBinding.tvArtist.text = " · ${item.artist}"
        viewBinding.ivDelete.setOnClickListener {
            audioPlayer.delete(item)
        }
    }
}