package me.wcy.music.search.playlist

import android.annotation.SuppressLint
import com.blankj.utilcode.util.SizeUtils
import top.wangchenyan.common.ext.context
import me.wcy.music.common.bean.PlaylistData
import me.wcy.music.databinding.ItemSearchPlaylistBinding
import me.wcy.music.utils.ConvertUtils
import me.wcy.music.utils.ImageUtils.loadCover
import me.wcy.music.utils.MusicUtils
import me.wcy.radapter3.RItemBinder

/**
 * Created by wangchenyan.top on 2023/9/21.
 */
class SearchPlaylistItemBinder(private val onItemClick: (PlaylistData) -> Unit) :
    RItemBinder<ItemSearchPlaylistBinding, PlaylistData>() {
    var keywords = ""

    @SuppressLint("SetTextI18n")
    override fun onBind(viewBinding: ItemSearchPlaylistBinding, item: PlaylistData, position: Int) {
        viewBinding.root.setOnClickListener {
            onItemClick(item)
        }
        viewBinding.ivCover.loadCover(item.getSmallCover(), SizeUtils.dp2px(4f))
        viewBinding.tvTitle.text = MusicUtils.keywordsTint(viewBinding.context, item.name, keywords)
        viewBinding.tvSubTitle.text = "${item.trackCount}首 , by ${item.creator.nickname} , 播放${
            ConvertUtils.formatPlayCount(item.playCount, 1)
        }次"
    }
}