package me.wcy.music.common.dialog.songmenu

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import com.blankj.utilcode.util.SizeUtils
import top.wangchenyan.common.widget.dialog.BottomDialog
import top.wangchenyan.common.widget.dialog.BottomDialogBuilder
import me.wcy.music.common.bean.SongData
import me.wcy.music.databinding.DialogSongMoreMenuBinding
import me.wcy.music.databinding.ItemSongMoreMenuBinding
import me.wcy.music.storage.db.entity.SongEntity
import me.wcy.music.utils.ImageUtils.loadCover
import me.wcy.music.utils.getSimpleArtist

/**
 * Created by wangchenyan.top on 2023/10/11.
 */
class SongMoreMenuDialog {
    private val context: Context
    private var songEntity: SongEntity? = null
    private var songData: SongData? = null
    private val items = mutableListOf<MenuItem>()

    constructor(context: Context, songEntity: SongEntity) {
        this.context = context
        this.songEntity = songEntity
    }

    constructor(context: Context, songData: SongData) {
        this.context = context
        this.songData = songData
    }

    fun setItems(items: List<MenuItem>) = apply {
        this.items.apply {
            clear()
            addAll(items)
        }
    }

    fun show() {
        BottomDialogBuilder(context)
            .contentViewBinding { dialog: BottomDialog, viewBinding: DialogSongMoreMenuBinding ->
                bindSongInfo(viewBinding)
                bindMenus(dialog, viewBinding)
            }
            .cancelable(true)
            .build()
            .show()
    }

    @SuppressLint("SetTextI18n")
    private fun bindSongInfo(viewBinding: DialogSongMoreMenuBinding) {
        val songEntity = songEntity
        val songData = songData
        if (songEntity != null) {
            viewBinding.ivCover.loadCover(songEntity.getSmallCover(), SizeUtils.dp2px(4f))
            viewBinding.tvTitle.text = "歌曲: ${songEntity.title}"
            viewBinding.tvArtist.text = songEntity.artist
        } else if (songData != null) {
            viewBinding.ivCover.loadCover(songData.al.getSmallCover(), SizeUtils.dp2px(4f))
            viewBinding.tvTitle.text = "歌曲: ${songData.name}"
            viewBinding.tvArtist.text = songData.getSimpleArtist()
        }
    }

    private fun bindMenus(dialog: BottomDialog, viewBinding: DialogSongMoreMenuBinding) {
        viewBinding.menuContainer.removeAllViews()
        items.forEach { item ->
            ItemSongMoreMenuBinding.inflate(
                LayoutInflater.from(context),
                viewBinding.menuContainer,
                true
            ).apply {
                root.text = item.name
                root.setOnClickListener {
                    dialog.dismiss()
                    item.onClick(it)
                }
            }
        }
    }
}