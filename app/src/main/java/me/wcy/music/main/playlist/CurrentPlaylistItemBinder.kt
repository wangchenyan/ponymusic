package me.wcy.music.main.playlist

import android.annotation.SuppressLint
import androidx.media3.common.MediaItem
import me.wcy.music.common.OnItemClickListener2
import me.wcy.music.databinding.ItemCurrentPlaylistBinding
import me.wcy.music.service.PlayerController
import me.wcy.radapter3.RItemBinder

/**
 * Created by wangchenyan.top on 2023/9/4.
 */
class CurrentPlaylistItemBinder(
    private val playerController: PlayerController,
    private val listener: OnItemClickListener2<MediaItem>
) :
    RItemBinder<ItemCurrentPlaylistBinding, MediaItem>() {
    @SuppressLint("SetTextI18n")
    override fun onBind(viewBinding: ItemCurrentPlaylistBinding, item: MediaItem, position: Int) {
        viewBinding.root.isSelected = (playerController.currentSong.value == item)
        viewBinding.root.setOnClickListener {
            listener.onItemClick(item, position)
        }
        viewBinding.tvTitle.text = item.mediaMetadata.title
        viewBinding.tvArtist.text = " · ${item.mediaMetadata.artist}"
        viewBinding.ivDelete.setOnClickListener {
            listener.onMoreClick(item, position)
        }
    }
}