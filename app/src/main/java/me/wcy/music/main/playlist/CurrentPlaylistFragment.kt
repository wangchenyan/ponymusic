package me.wcy.music.main.playlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.buildSpannedString
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.util.ActivityUtils
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import top.wangchenyan.common.ext.getColorEx
import top.wangchenyan.common.ext.showConfirmDialog
import top.wangchenyan.common.ext.viewBindings
import top.wangchenyan.common.widget.CustomSpan.appendStyle
import me.wcy.music.R
import me.wcy.music.databinding.FragmentCurrentPlaylistBinding
import me.wcy.music.main.playing.PlayingActivity
import me.wcy.music.service.AudioPlayer
import me.wcy.music.service.PlayMode
import me.wcy.music.storage.db.entity.SongEntity
import me.wcy.radapter3.RAdapter
import javax.inject.Inject

/**
 * Created by wangchenyan.top on 2023/10/13.
 */
@AndroidEntryPoint
class CurrentPlaylistFragment : BottomSheetDialogFragment() {
    private val viewBinding by viewBindings<FragmentCurrentPlaylistBinding>()
    private val adapter by lazy { RAdapter<SongEntity>() }

    @Inject
    lateinit var audioPlayer: AudioPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme)
    }

    override fun getTheme(): Int {
        return R.style.BottomSheetDialogTheme
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initData()
    }

    private fun initView() {
        viewBinding.root.setOnClickListener {
            dismissAllowingStateLoss()
        }
        viewBinding.llPlayMode.setOnClickListener {
            switchPlayMode()
        }
        viewBinding.btnClear.setOnClickListener {
            showConfirmDialog(message = "确认清空播放列表？") {
                audioPlayer.clearPlaylist()
                dismissAllowingStateLoss()
                ActivityUtils.finishActivity(PlayingActivity::class.java)
            }
        }

        adapter.register(CurrentPlaylistItemBinder(audioPlayer))
        viewBinding.recyclerView.adapter = adapter
    }

    private fun initData() {
        lifecycleScope.launch {
            audioPlayer.playMode.collectLatest { playMode ->
                viewBinding.ivMode.setImageLevel(playMode.value)
                viewBinding.tvPlayMode.setText(playMode.nameRes)
            }
        }

        audioPlayer.playlist.observe(this) { playlist ->
            playlist ?: return@observe
            val size = playlist.size
            viewBinding.tvTitle.text = buildSpannedString {
                append("当前播放")
                if (size > 0) {
                    appendStyle(
                        "($size)",
                        color = context.getColorEx(R.color.common_text_h2_color),
                        isBold = true
                    )
                }
            }
            adapter.refresh(playlist)
        }
        audioPlayer.currentSong.observe(this) {
            adapter.notifyDataSetChanged()
        }
    }

    private fun switchPlayMode() {
        val mode = when (audioPlayer.playMode.value) {
            PlayMode.Loop -> PlayMode.Shuffle
            PlayMode.Shuffle -> PlayMode.Single
            PlayMode.Single -> PlayMode.Loop
        }
        audioPlayer.setPlayMode(mode)
    }

    companion object {
        const val TAG = "CurrentPlaylistFragment"
        fun newInstance(): CurrentPlaylistFragment {
            return CurrentPlaylistFragment()
        }
    }
}