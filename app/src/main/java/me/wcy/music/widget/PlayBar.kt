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
import me.wcy.music.R
import me.wcy.music.consts.RoutePath
import me.wcy.music.databinding.LayoutPlayBarBinding
import me.wcy.music.main.playlist.CurrentPlaylistFragment
import me.wcy.music.service.PlayServiceModule.playerController
import me.wcy.music.service.PlayState
import me.wcy.music.utils.getDuration
import me.wcy.music.utils.getSmallCover
import me.wcy.router.CRouter
import top.wangchenyan.common.CommonApp
import top.wangchenyan.common.ext.findActivity
import top.wangchenyan.common.ext.findLifecycleOwner
import top.wangchenyan.common.ext.getColor
import top.wangchenyan.common.ext.loadAvatar
import top.wangchenyan.common.widget.CustomSpan.appendStyle

/**
 * Created by wangchenyan.top on 2023/9/4.
 */
class PlayBar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {
    private val viewBinding: LayoutPlayBarBinding
    private val playerController by lazy {
        CommonApp.app.playerController()
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
        viewBinding.flPlay.setOnClickListener {
            playerController.playPause()
        }
        viewBinding.ivNext.setOnClickListener {
            playerController.next()
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
        playerController.currentSong.observe(lifecycleOwner) { currentSong ->
            if (currentSong != null) {
                isVisible = true
                viewBinding.ivCover.loadAvatar(currentSong.getSmallCover())
                viewBinding.tvTitle.text = buildSpannedString {
                    append(currentSong.mediaMetadata.title)
                    appendStyle(
                        " - ${currentSong.mediaMetadata.artist}",
                        color = getColor(R.color.common_text_h2_color)
                    )
                }
                viewBinding.progressBar.max = currentSong.mediaMetadata.getDuration().toInt()
                viewBinding.progressBar.progress = playerController.playProgress.value.toInt()
            } else {
                isVisible = false
            }
        }

        lifecycleOwner.lifecycleScope.launch {
            playerController.playState.collectLatest { playState ->
                when (playState) {
                    PlayState.Preparing -> {
                        viewBinding.flPlay.isEnabled = false
                        viewBinding.ivPlay.isSelected = false
                        viewBinding.loadingProgress.isVisible = true
                    }

                    PlayState.Playing -> {
                        viewBinding.flPlay.isEnabled = true
                        viewBinding.ivPlay.isSelected = true
                        viewBinding.loadingProgress.isVisible = false
                    }

                    else -> {
                        viewBinding.flPlay.isEnabled = true
                        viewBinding.ivPlay.isSelected = false
                        viewBinding.loadingProgress.isVisible = false
                    }
                }

                if (playState.isPlaying) {
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
            playerController.playProgress.collectLatest {
                viewBinding.progressBar.progress = it.toInt()
            }
        }
    }
}