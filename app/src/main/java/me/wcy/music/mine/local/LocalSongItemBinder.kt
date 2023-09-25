package me.wcy.music.mine.local

import com.blankj.utilcode.util.SizeUtils
import me.wcy.music.databinding.ItemLocalSongBinding
import me.wcy.music.storage.db.entity.SongEntity
import me.wcy.music.utils.ImageUtils.loadCover
import me.wcy.music.utils.MusicUtils
import me.wcy.radapter3.RItemBinder

/**
 * Created by wangchenyan.top on 2023/8/30.
 */
class LocalSongItemBinder(private val onItemClick: (SongEntity) -> Unit) :
    RItemBinder<ItemLocalSongBinding, SongEntity>() {

    override fun onBind(viewBinding: ItemLocalSongBinding, item: SongEntity, position: Int) {
        viewBinding.root.setOnClickListener {
            onItemClick(item)
        }
        viewBinding.ivCover.loadCover(item.albumCover, SizeUtils.dp2px(4f))
        viewBinding.tvTitle.text = item.title
        viewBinding.tvArtist.text = MusicUtils.getArtistAndAlbum(item.artist, item.album)
    }
}