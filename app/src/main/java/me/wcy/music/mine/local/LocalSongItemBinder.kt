package me.wcy.music.mine.local

import me.wcy.music.common.OnSongItemClickListener
import me.wcy.music.databinding.ItemLocalSongBinding
import me.wcy.music.storage.db.entity.SongEntity
import me.wcy.music.utils.MusicUtils
import me.wcy.radapter3.RItemBinder

/**
 * Created by wangchenyan.top on 2023/8/30.
 */
class LocalSongItemBinder(
    private val listener: OnSongItemClickListener<SongEntity>
) : RItemBinder<ItemLocalSongBinding, SongEntity>() {

    override fun onBind(viewBinding: ItemLocalSongBinding, item: SongEntity, position: Int) {
        viewBinding.root.setOnClickListener {
            listener.onItemClick(item, position)
        }
        viewBinding.ivMore.setOnClickListener {
            listener.onMoreClick(item, position)
        }
        viewBinding.tvTitle.text = item.title
        viewBinding.tvArtist.text = MusicUtils.getArtistAndAlbum(item.artist, item.album)
    }
}