package me.wcy.music.main.playing

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.media.AudioManager
import android.os.Bundle
import android.text.format.DateUtils
import android.view.View
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import jp.wasabeef.blurry.Blurry
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import me.wcy.common.ext.toast
import me.wcy.common.ext.viewBindings
import me.wcy.common.utils.image.ImageUtils
import me.wcy.lrcview.LrcView
import me.wcy.music.R
import me.wcy.music.common.BaseMusicActivity
import me.wcy.music.databinding.ActivityPlayingBinding
import me.wcy.music.ext.registerReceiverCompat
import me.wcy.music.service.AudioPlayer
import me.wcy.music.service.PlayModeEnum
import me.wcy.music.storage.db.entity.SongEntity
import me.wcy.music.storage.preference.MusicPreferences
import me.wcy.music.utils.FileUtils
import me.wcy.music.utils.TimeUtils
import me.wcy.router.annotation.Route
import java.io.File
import javax.inject.Inject

/**
 * Created by wangchenyan.top on 2023/9/4.
 */
@Route("/playing")
@AndroidEntryPoint
class PlayingActivity : BaseMusicActivity() {
    private val viewBinding by viewBindings<ActivityPlayingBinding>()

    @Inject
    lateinit var audioPlayer: AudioPlayer

    private val mAudioManager by lazy {
        getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }

    private val defaultCoverBitmap by lazy {
        BitmapFactory.decodeResource(resources, R.drawable.bg_playing_default_cover)
    }

    private var mLastProgress = 0
    private var isDraggingProgress = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)

        initTitle()
        initVolume()
        initCover()
        initLrc()
        switchCoverLrc(true)
        initPlayControl()
        updatePlayMode()
        initData()
    }

    private fun initTitle() {
        viewBinding.ivBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun initVolume() {
        viewBinding.sbVolume.max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        viewBinding.sbVolume.progress = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        val filter = IntentFilter(VOLUME_CHANGED_ACTION)
        registerReceiverCompat(volumeReceiver, filter)
    }

    private fun initCover() {
        viewBinding.albumCoverView.initNeedle(audioPlayer.playState.value.isPlaying)
        viewBinding.albumCoverView.setOnClickListener {
            switchCoverLrc(false)
        }
    }

    private fun initLrc() {
        viewBinding.lrcView.setDraggable(true) { view, time ->
            if (audioPlayer.playState.value.isPlaying || audioPlayer.playState.value.isPausing) {
                audioPlayer.seekTo(time.toInt())
                if (audioPlayer.playState.value.isPausing) {
                    audioPlayer.playPause()
                }
                true
            }
            false
        }
        viewBinding.lrcView.setOnTapListener { view: LrcView?, x: Float, y: Float ->
            switchCoverLrc(true)
        }
    }

    private fun initPlayControl() {
        viewBinding.ivMode.setOnClickListener {
            switchPlayMode()
        }
        viewBinding.ivPlay.setOnClickListener {
            audioPlayer.playPause()
        }
        viewBinding.ivPrev.setOnClickListener {
            audioPlayer.prev()
        }
        viewBinding.ivNext.setOnClickListener {
            audioPlayer.next()
        }
        viewBinding.sbProgress.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (Math.abs(progress - mLastProgress) >= DateUtils.SECOND_IN_MILLIS) {
                    viewBinding.tvCurrentTime.text =
                        TimeUtils.formatTime("mm:ss", progress.toLong())
                    mLastProgress = progress
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                isDraggingProgress = true
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                seekBar ?: return
                isDraggingProgress = false
                if (audioPlayer.playState.value.isPlaying
                    || audioPlayer.playState.value.isPausing
                ) {
                    val progress = seekBar.progress
                    audioPlayer.seekTo(progress)
                    if (viewBinding.lrcView.hasLrc()) {
                        viewBinding.lrcView.updateTime(progress.toLong())
                    }
                } else {
                    seekBar.progress = 0
                }
            }
        })
        viewBinding.sbVolume.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                seekBar ?: return
                mAudioManager.setStreamVolume(
                    AudioManager.STREAM_MUSIC,
                    seekBar.progress,
                    AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE
                )
            }
        })
    }

    private fun initData() {
        audioPlayer.currentSong.observe(this) { song ->
            if (song != null) {
                viewBinding.tvTitle.text = song.title
                viewBinding.tvArtist.text = song.artist
                viewBinding.sbProgress.progress = audioPlayer.playProgress.value.toInt()
                viewBinding.sbProgress.secondaryProgress = 0
                viewBinding.sbProgress.max = song.duration.toInt()
                mLastProgress = 0
                viewBinding.tvCurrentTime.setText(R.string.play_time_start)
                viewBinding.tvTotalTime.text = TimeUtils.formatTime("mm:ss", song.duration)
                updateCover(song)
                updateLrc(song)
                if (audioPlayer.playState.value.isPlaying || audioPlayer.playState.value.isPreparing) {
                    viewBinding.ivPlay.isSelected = true
                    viewBinding.albumCoverView.start()
                } else {
                    viewBinding.ivPlay.isSelected = false
                    viewBinding.albumCoverView.pause()
                }
            } else {
                finish()
            }
        }

        lifecycleScope.launch {
            audioPlayer.playState.collectLatest { playState ->
                if (playState.isPlaying) {
                    viewBinding.ivPlay.isSelected = true
                    viewBinding.albumCoverView.start()
                } else {
                    viewBinding.ivPlay.isSelected = false
                    viewBinding.albumCoverView.pause()
                }
            }
        }

        lifecycleScope.launch {
            audioPlayer.playProgress.collectLatest { progress ->
                if (isDraggingProgress.not()) {
                    viewBinding.sbProgress.progress = progress.toInt()
                }
                if (viewBinding.lrcView.hasLrc()) {
                    viewBinding.lrcView.updateTime(progress)
                }
            }
        }

        lifecycleScope.launch {
            audioPlayer.bufferingPercent.collectLatest { percent ->
                viewBinding.sbProgress.secondaryProgress =
                    viewBinding.sbProgress.max * percent / 100
            }
        }
    }

    private fun updatePlayMode() {
        val mode = MusicPreferences.playMode
        viewBinding.ivMode.setImageLevel(mode)
    }

    private fun updateCover(song: SongEntity) {
        viewBinding.albumCoverView.setCoverBitmap(defaultCoverBitmap)
        viewBinding.ivPlayingBg.setImageResource(R.drawable.bg_playing_default)
        ImageUtils.loadBitmap(song.albumCover) {
            if (it.isSuccessWithData()) {
                val bitmap = it.getDataOrThrow()
                viewBinding.albumCoverView.setCoverBitmap(bitmap)
                Blurry.with(this).from(bitmap).into(viewBinding.ivPlayingBg)
            }
        }
    }

    private fun updateLrc(song: SongEntity) {
        if (song.type == SongEntity.LOCAL) {
            val lrcPath = FileUtils.getLrcFilePath(song)
            if (lrcPath?.isNotEmpty() == true) {
                loadLrc(lrcPath)
            } else {
                setLrcLabel("暂无歌词")
            }
        } else {
            val lrcPath =
                FileUtils.getLrcDir() + "/" + FileUtils.getLrcFileName(song.artist, song.title)
            loadLrc(lrcPath)
        }
    }

    private fun loadLrc(path: String?) {
        val file = File(path)
        viewBinding.lrcView.loadLrc(file)
    }

    private fun setLrcLabel(label: String) {
        viewBinding.lrcView.setLabel(label)
    }

    private fun switchCoverLrc(showCover: Boolean) {
        viewBinding.albumCoverView.visibility =
            if (showCover) View.VISIBLE else View.GONE
        viewBinding.lrcLayout.visibility = if (showCover) View.GONE else View.VISIBLE
    }

    private fun switchPlayMode() {
        var mode: PlayModeEnum = PlayModeEnum.valueOf(MusicPreferences.playMode)
        when (mode) {
            PlayModeEnum.LOOP -> {
                mode = PlayModeEnum.SHUFFLE
                toast(R.string.play_mode_shuffle)
            }

            PlayModeEnum.SHUFFLE -> {
                mode = PlayModeEnum.SINGLE
                toast(R.string.play_mode_single)
            }

            PlayModeEnum.SINGLE -> {
                mode = PlayModeEnum.LOOP
                toast(R.string.play_mode_loop)
            }
        }
        MusicPreferences.playMode = mode.value()
        updatePlayMode()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(volumeReceiver)
    }

    private val volumeReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            viewBinding.sbVolume.progress =
                mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        }
    }

    companion object {
        private const val VOLUME_CHANGED_ACTION = "android.media.VOLUME_CHANGED_ACTION"
    }
}