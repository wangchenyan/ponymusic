package me.wcy.music.widget

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import androidx.core.text.buildSpannedString
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import me.wcy.common.CommonApp
import me.wcy.common.ext.findActivity
import me.wcy.common.ext.findLifecycleOwner
import me.wcy.common.ext.getColor
import me.wcy.common.ext.load
import me.wcy.common.widget.CustomSpan.appendStyle
import me.wcy.music.R
import me.wcy.music.consts.RoutePath
import me.wcy.music.databinding.LayoutPlayBarBinding
import me.wcy.music.main.playlist.CurrentPlaylistFragment
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
    private val rotateAnimator: ObjectAnimator

    init {
        id = R.id.play_bar
        viewBinding = LayoutPlayBarBinding.inflate(LayoutInflater.from(context), this, true)

        rotateAnimator = ObjectAnimator.ofFloat(viewBinding.ivCover, "rotation", 0f, 360f).apply {
            duration = 20000
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ObjectAnimator.RESTART
            interpolator = LinearInterpolator()
        }

        initView()
        context.findLifecycleOwner()?.let {
            initData(it)
        }
    }

    private fun initView() {
        viewBinding.root.setOnClickListener {
            CRouter.with(context).url(RoutePath.PLAYING).start()
        }
        viewBinding.ivPlay.setOnClickListener {
            audioPlayer.playPause()
        }
        viewBinding.ivNext.setOnClickListener {
            audioPlayer.next()
        }
        viewBinding.ivPlaylist.setOnClickListener {
            val activity = context.findActivity()
            if (activity is FragmentActivity) {
                CurrentPlaylistFragment.newInstance()
                    .show(activity.supportFragmentManager, CurrentPlaylistFragment.TAG)
            }
        }
    }

    private fun initData(lifecycleOwner: LifecycleOwner) {
        audioPlayer.currentSong.observe(lifecycleOwner) { currentSong ->
            if (currentSong != null) {
                isVisible = true
                viewBinding.ivCover.load(currentSong.getSmallCover(), true)
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
                val isPlaying = playState.isPreparing || playState.isPlaying
                viewBinding.ivPlay.isSelected = isPlaying
                if (isPlaying) {
                    if (rotateAnimator.isPaused) {
                        rotateAnimator.resume()
                    } else if (rotateAnimator.isStarted.not()) {
                        rotateAnimator.start()
                    }
                } else {
                    if (rotateAnimator.isRunning) {
                        rotateAnimator.pause()
                    }
                }
            }
        }

        lifecycleOwner.lifecycleScope.launch {
            audioPlayer.playProgress.collectLatest {
                viewBinding.progressBar.progress = it.toInt()
            }
        }
    }
}