package me.wcy.music.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.text.buildSpannedString
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import me.wcy.common.CommonApp
import me.wcy.common.ext.getColor
import me.wcy.common.ext.load
import me.wcy.common.widget.CustomSpan.appendStyle
import me.wcy.music.R
import me.wcy.music.databinding.LayoutPlayBarBinding
import me.wcy.music.ext.findLifecycleOwner
import me.wcy.music.service.AudioPlayerModule.Companion.audioPlayer
import me.wcy.router.CRouter

/**
 * Created by wangchenyan.top on 2023/9/4.
 */
class PlayBar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {
    private val viewBinding: LayoutPlayBarBinding
    private val audioPlayer by lazy {
        CommonApp.app.audioPlayer()
    }

    init {
        id = R.id.play_bar
        viewBinding = LayoutPlayBarBinding.inflate(LayoutInflater.from(context), this, true)

        initView()
        context.findLifecycleOwner()?.let {
            initData(it)
        }
    }

    private fun initView() {
        viewBinding.root.setOnClickListener {
            CRouter.with(context).url("/playing").start()
        }
        viewBinding.ivPlay.setOnClickListener {
            audioPlayer.playPause()
        }
        viewBinding.ivNext.setOnClickListener {
            audioPlayer.next()
        }
        viewBinding.ivPlaylist.setOnClickListener {
            CRouter.with(context).url("/playlist").start()
        }
    }

    private fun initData(lifecycleOwner: LifecycleOwner) {
        audioPlayer.currentSong.observe(lifecycleOwner) { currentSong ->
            if (currentSong != null) {
                isVisible = true
                viewBinding.ivCover.load(currentSong.albumCover, true)
                viewBinding.tvTitle.text = buildSpannedString {
                    append(currentSong.title)
                    appendStyle(
                        " - ${currentSong.artist}",
                        color = getColor(R.color.common_text_h2_color)
                    )
                }
                viewBinding.progressBar.max = currentSong.duration.toInt()
                viewBinding.progressBar.progress = audioPlayer.playProgress.value.toInt()
            } else {
                isVisible = false
            }
        }

        lifecycleOwner.lifecycleScope.launch {
            audioPlayer.playState.collectLatest { playState ->
                viewBinding.ivPlay.isSelected = playState.isPreparing || playState.isPlaying
            }
        }

        lifecycleOwner.lifecycleScope.launch {
            audioPlayer.playProgress.collectLatest {
                viewBinding.progressBar.progress = it.toInt()
            }
        }
    }
}