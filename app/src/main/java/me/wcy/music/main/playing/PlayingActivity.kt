package me.wcy.music.main.playing

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.media.AudioManager
import android.os.Bundle
import android.text.format.DateUtils
import android.util.Log
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.gyf.immersionbar.ImmersionBar
import dagger.hilt.android.AndroidEntryPoint
import jp.wasabeef.blurry.Blurry
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import me.wcy.lrcview.LrcView
import me.wcy.music.R
import me.wcy.music.common.BaseMusicActivity
import me.wcy.music.consts.RoutePath
import me.wcy.music.databinding.ActivityPlayingBinding
import me.wcy.music.discover.DiscoverApi
import me.wcy.music.ext.registerReceiverCompat
import me.wcy.music.main.playlist.CurrentPlaylistFragment
import me.wcy.music.service.AudioPlayer
import me.wcy.music.service.likesong.LikeSongProcessor
import me.wcy.music.service.PlayMode
import me.wcy.music.storage.LrcCache
import me.wcy.music.storage.db.entity.SongEntity
import me.wcy.music.utils.TimeUtils
import me.wcy.router.annotation.Route
import top.wangchenyan.common.ext.toast
import top.wangchenyan.common.ext.viewBindings
import top.wangchenyan.common.utils.StatusBarUtils
import top.wangchenyan.common.utils.image.ImageUtils
import java.io.File
import javax.inject.Inject
import kotlin.math.abs

/**
 * Created by wangchenyan.top on 2023/9/4.
 */
@Route(RoutePath.PLAYING)
@AndroidEntryPoint
class PlayingActivity : BaseMusicActivity() {
    private val viewBinding by viewBindings<ActivityPlayingBinding>()

    @Inject
    lateinit var audioPlayer: AudioPlayer

    @Inject
    lateinit var likeSongProcessor: LikeSongProcessor

    private val audioManager by lazy {
        getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }

    private val defaultCoverBitmap by lazy {
        BitmapFactory.decodeResource(resources, R.drawable.bg_playing_default_cover)
    }

    private var loadLrcJob: Job? = null

    private var lastProgress = 0
    private var isDraggingProgress = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)

        initTitle()
        initVolume()
        initCover()
        initLrc()
        initActions()
        initPlayControl()
        initData()
        switchCoverLrc(true)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        if (StatusBarUtils.isSupportStatusBarTransparent()) {
            ImmersionBar.with(this)
                .transparentNavigationBar()
                .navigationBarDarkIcon(false)
                .init()
        }
    }

    private fun initTitle() {
        viewBinding.ivClose.setOnClickListener {
            onBackPressed()
        }
    }

    private fun initVolume() {
        viewBinding.sbVolume.max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        viewBinding.sbVolume.progress = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        val filter = IntentFilter(VOLUME_CHANGED_ACTION)
        registerReceiverCompat(volumeReceiver, filter)
    }

    private fun initCover() {
        viewBinding.albumCoverView.initNeedle(audioPlayer.playState.value.isPlaying)
        viewBinding.clAlbumCover.setOnClickListener {
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
                return@setDraggable true
            }
            return@setDraggable false
        }
        viewBinding.lrcView.setOnTapListener { view: LrcView?, x: Float, y: Float ->
            switchCoverLrc(true)
        }
    }

    private fun initActions() {
        viewBinding.ivLike.setOnClickListener {
            lifecycleScope.launch {
                val song = audioPlayer.currentSong.value ?: return@launch
                val res = likeSongProcessor.like(this@PlayingActivity, song.songId)
                if (res.isSuccess()) {
                    updateLikeState(song)
                } else {
                    toast(res.msg)
                }
            }
        }
    }

    private fun initPlayControl() {
        lifecycleScope.launch {
            audioPlayer.playMode.collectLatest { playMode ->
                viewBinding.ivMode.setImageLevel(playMode.value)
            }
        }

        val lp = viewBinding.navigationBarPlaceholder.layoutParams
        lp.height = ImmersionBar.getNavigationBarHeight(this)
        viewBinding.navigationBarPlaceholder.layoutParams = lp

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
        viewBinding.ivPlaylist.setOnClickListener {
            CurrentPlaylistFragment.newInstance()
                .show(supportFragmentManager, CurrentPlaylistFragment.TAG)
        }
        viewBinding.sbProgress.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (abs(progress - lastProgress) >= DateUtils.SECOND_IN_MILLIS) {
                    viewBinding.tvCurrentTime.text = TimeUtils.formatMs(progress.toLong())
                    lastProgress = progress
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
                audioManager.setStreamVolume(
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
                viewBinding.sbProgress.max = song.duration.toInt()
                viewBinding.sbProgress.progress = audioPlayer.playProgress.value.toInt()
                viewBinding.sbProgress.secondaryProgress = 0
                lastProgress = 0
                viewBinding.tvCurrentTime.text = TimeUtils.formatMs(audioPlayer.playProgress.value)
                viewBinding.tvTotalTime.text = TimeUtils.formatMs(song.duration)
                updateCover(song)
                updateLrc(song)
                viewBinding.albumCoverView.reset()
                if (audioPlayer.playState.value.isPlaying || audioPlayer.playState.value.isPreparing) {
                    viewBinding.ivPlay.isSelected = true
                    viewBinding.albumCoverView.start()
                } else {
                    viewBinding.ivPlay.isSelected = false
                    viewBinding.albumCoverView.pause()
                }
                updateLikeState(song)
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

    private fun updateCover(song: SongEntity) {
        viewBinding.albumCoverView.setCoverBitmap(defaultCoverBitmap)
        viewBinding.ivPlayingBg.setImageResource(R.drawable.bg_playing_default)
        ImageUtils.loadBitmap(song.getLargeCover()) {
            if (it.isSuccessWithData()) {
                val bitmap = it.getDataOrThrow()
                viewBinding.albumCoverView.setCoverBitmap(bitmap)
                Blurry.with(this).sampling(10).from(bitmap).into(viewBinding.ivPlayingBg)
            }
        }
    }

    private fun updateLrc(song: SongEntity) {
        loadLrcJob?.cancel()
        loadLrcJob = null
        val lrcPath = LrcCache.getLrcFilePath(song)
        if (lrcPath?.isNotEmpty() == true) {
            loadLrc(lrcPath)
            return
        }
        if (song.isLocal()) {
            setLrcLabel("暂无歌词")
        } else {
            setLrcLabel("歌词加载中…")
            loadLrcJob = lifecycleScope.launch {
                kotlin.runCatching {
                    val lrcWrap = DiscoverApi.get().getLrc(song.songId)
                    if (lrcWrap.code == 200 && lrcWrap.lrc.isValid()) {
                        lrcWrap.lrc
                    } else {
                        throw IllegalStateException("lrc is invalid")
                    }
                }.onSuccess {
                    val file = LrcCache.saveLrcFile(song, it.lyric)
                    loadLrc(file.path)
                }.onFailure {
                    Log.e(TAG, "load lrc error", it)
                    setLrcLabel("歌词加载失败")
                }
            }
        }
    }

    private fun loadLrc(path: String) {
        val file = File(path)
        viewBinding.lrcView.loadLrc(file)
    }

    private fun setLrcLabel(label: String) {
        viewBinding.lrcView.setLabel(label)
    }

    private fun switchCoverLrc(showCover: Boolean) {
        viewBinding.clAlbumCover.isVisible = showCover
        viewBinding.lrcLayout.isVisible = showCover.not()
    }

    private fun switchPlayMode() {
        val mode = when (audioPlayer.playMode.value) {
            PlayMode.Loop -> PlayMode.Shuffle
            PlayMode.Shuffle -> PlayMode.Single
            PlayMode.Single -> PlayMode.Loop
        }
        toast(mode.nameRes)
        audioPlayer.setPlayMode(mode)
    }

    private fun updateLikeState(song: SongEntity) {
        viewBinding.ivLike.isSelected = likeSongProcessor.isLiked(song.songId)
    }

    override fun getNavigationBarColor(): Int {
        return R.color.black
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(volumeReceiver)
    }

    private val volumeReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            viewBinding.sbVolume.progress = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        }
    }

    companion object {
        private const val TAG = "PlayingActivity"
        private const val VOLUME_CHANGED_ACTION = "android.media.VOLUME_CHANGED_ACTION"
    }
}