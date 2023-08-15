package me.wcy.music.fragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.os.Bundle
import android.text.TextUtils
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import me.wcy.lrcview.LrcView
import me.wcy.lrcview.LrcView.OnPlayClickListener
import me.wcy.music.R
import me.wcy.music.constants.Actions
import me.wcy.music.enums.PlayModeEnum
import me.wcy.music.executor.SearchLrc
import me.wcy.music.model.Music
import me.wcy.music.service.AudioPlayer
import me.wcy.music.service.OnPlayerEventListener
import me.wcy.music.storage.preference.Preferences
import me.wcy.music.utils.CoverLoader
import me.wcy.music.utils.FileUtils
import me.wcy.music.utils.ScreenUtils
import me.wcy.music.utils.SystemUtils
import me.wcy.music.utils.ToastUtils
import me.wcy.music.utils.binding.Bind
import me.wcy.music.widget.AlbumCoverView
import java.io.File

/**
 * 正在播放界面
 * Created by wcy on 2015/11/27.
 */
class PlayFragment : BaseFragment(), View.OnClickListener, OnSeekBarChangeListener,
    OnPlayerEventListener, OnPlayClickListener {
    @Bind(R.id.ll_content)
    lateinit var llContent: LinearLayout

    @Bind(R.id.iv_play_page_bg)
    lateinit var ivPlayingBg: ImageView

    @Bind(R.id.iv_back)
    lateinit var ivBack: ImageView

    @Bind(R.id.tv_title)
    lateinit var tvTitle: TextView

    @Bind(R.id.tv_artist)
    lateinit var tvArtist: TextView

    @Bind(R.id.album_cover_view)
    lateinit var mAlbumCoverView: AlbumCoverView

    @Bind(R.id.ll_lrc)
    lateinit var lrcLayout: View

    @Bind(R.id.sb_volume)
    lateinit var sbVolume: SeekBar

    @Bind(R.id.lrc_view)
    lateinit var mLrcView: LrcView

    @Bind(R.id.sb_progress)
    lateinit var sbProgress: SeekBar

    @Bind(R.id.tv_current_time)
    lateinit var tvCurrentTime: TextView

    @Bind(R.id.tv_total_time)
    lateinit var tvTotalTime: TextView

    @Bind(R.id.iv_mode)
    lateinit var ivMode: ImageView

    @Bind(R.id.iv_play)
    lateinit var ivPlay: ImageView

    @Bind(R.id.iv_next)
    lateinit var ivNext: ImageView

    @Bind(R.id.iv_prev)
    lateinit var ivPrev: ImageView
    
    private var mAudioManager: AudioManager? = null
    private var mLastProgress = 0
    private var isDraggingProgress = false
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_play, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initSystemBar()
        initCoverLrc()
        initPlayMode()
        onChangeImpl(AudioPlayer.get().playMusic)
        AudioPlayer.get().addOnPlayEventListener(this)
    }

    override fun onResume() {
        super.onResume()
        val filter = IntentFilter(Actions.VOLUME_CHANGED_ACTION)
        requireContext().registerReceiver(mVolumeReceiver, filter)
    }

    override fun setListener() {
        ivBack.setOnClickListener(this)
        ivMode.setOnClickListener(this)
        ivPlay.setOnClickListener(this)
        ivPrev.setOnClickListener(this)
        ivNext.setOnClickListener(this)
        sbProgress.setOnSeekBarChangeListener(this)
        sbVolume.setOnSeekBarChangeListener(this)
    }

    /**
     * 沉浸式状态栏
     */
    private fun initSystemBar() {
        val top = ScreenUtils.statusBarHeight
        llContent.setPadding(0, top, 0, 0)
    }

    private fun initCoverLrc() {
        mAlbumCoverView.initNeedle(AudioPlayer.get().isPlaying)
        mAlbumCoverView.setOnClickListener { v: View? -> switchCoverLrc(false) }
        mLrcView.setDraggable(true, this)
        mLrcView.setOnTapListener { view: LrcView?, x: Float, y: Float -> switchCoverLrc(true) }
        initVolume()
        switchCoverLrc(true)
    }

    private fun initVolume() {
        mAudioManager = requireContext().getSystemService(Context.AUDIO_SERVICE) as AudioManager
        sbVolume.max = mAudioManager!!.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        sbVolume.progress = mAudioManager!!.getStreamVolume(AudioManager.STREAM_MUSIC)
    }

    private fun initPlayMode() {
        val mode = Preferences.playMode
        ivMode.setImageLevel(mode)
    }

    private fun switchCoverLrc(showCover: Boolean) {
        mAlbumCoverView.visibility =
            if (showCover) View.VISIBLE else View.GONE
        lrcLayout.visibility = if (showCover) View.GONE else View.VISIBLE
    }

    override fun onChange(music: Music?) {
        onChangeImpl(music)
    }

    override fun onPlayerStart() {
        ivPlay!!.isSelected = true
        mAlbumCoverView!!.start()
    }

    override fun onPlayerPause() {
        ivPlay!!.isSelected = false
        mAlbumCoverView!!.pause()
    }

    /**
     * 更新播放进度
     */
    override fun onPublish(progress: Int) {
        if (!isDraggingProgress) {
            sbProgress!!.progress = progress
        }
        if (mLrcView!!.hasLrc()) {
            mLrcView.updateTime(progress.toLong())
        }
    }

    override fun onBufferingUpdate(percent: Int) {
        sbProgress!!.secondaryProgress = sbProgress.max * 100 / percent
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.iv_back -> onBackPressed()
            R.id.iv_mode -> switchPlayMode()
            R.id.iv_play -> play()
            R.id.iv_next -> next()
            R.id.iv_prev -> prev()
        }
    }

    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        if (seekBar === sbProgress) {
            if (Math.abs(progress - mLastProgress) >= DateUtils.SECOND_IN_MILLIS) {
                tvCurrentTime!!.text = formatTime(progress.toLong())
                mLastProgress = progress
            }
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {
        if (seekBar === sbProgress) {
            isDraggingProgress = true
        }
    }

    override fun onStopTrackingTouch(seekBar: SeekBar) {
        if (seekBar === sbProgress) {
            isDraggingProgress = false
            if (AudioPlayer.get().isPlaying || AudioPlayer.get()
                    .isPausing
            ) {
                val progress = seekBar.progress
                AudioPlayer.get().seekTo(progress)
                if (mLrcView!!.hasLrc()) {
                    mLrcView.updateTime(progress.toLong())
                }
            } else {
                seekBar.progress = 0
            }
        } else if (seekBar === sbVolume) {
            mAudioManager!!.setStreamVolume(
                AudioManager.STREAM_MUSIC, seekBar.progress,
                AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE
            )
        }
    }

    override fun onPlayClick(view: LrcView, time: Long): Boolean {
        if (AudioPlayer.get().isPlaying || AudioPlayer.get().isPausing) {
            AudioPlayer.get().seekTo(time.toInt())
            if (AudioPlayer.get().isPausing) {
                AudioPlayer.get().playPause()
            }
            return true
        }
        return false
    }

    private fun onChangeImpl(music: Music?) {
        if (music == null) {
            return
        }
        tvTitle.setText(music.title)
        tvArtist.setText(music.artist)
        sbProgress!!.progress = AudioPlayer.get().audioPosition.toInt()
        sbProgress.secondaryProgress = 0
        sbProgress.max = music.duration.toInt()
        mLastProgress = 0
        tvCurrentTime!!.setText(R.string.play_time_start)
        tvTotalTime!!.text = formatTime(music.duration)
        setCoverAndBg(music)
        setLrc(music)
        if (AudioPlayer.get().isPlaying || AudioPlayer.get().isPreparing) {
            ivPlay!!.isSelected = true
            mAlbumCoverView!!.start()
        } else {
            ivPlay!!.isSelected = false
            mAlbumCoverView!!.pause()
        }
    }

    private fun play() {
        AudioPlayer.get().playPause()
    }

    private operator fun next() {
        AudioPlayer.get().next()
    }

    private fun prev() {
        AudioPlayer.get().prev()
    }

    private fun switchPlayMode() {
        var mode: PlayModeEnum = PlayModeEnum.valueOf(Preferences.playMode)
        when (mode) {
            PlayModeEnum.LOOP -> {
                mode = PlayModeEnum.SHUFFLE
                ToastUtils.show(R.string.mode_shuffle)
            }

            PlayModeEnum.SHUFFLE -> {
                mode = PlayModeEnum.SINGLE
                ToastUtils.show(R.string.mode_one)
            }

            PlayModeEnum.SINGLE -> {
                mode = PlayModeEnum.LOOP
                ToastUtils.show(R.string.mode_loop)
            }
        }
        Preferences.savePlayMode(mode.value())
        initPlayMode()
    }

    private fun onBackPressed() {
        requireActivity().onBackPressed()
        ivBack!!.isEnabled = false
        handler!!.postDelayed({ ivBack.isEnabled = true }, 300)
    }

    private fun setCoverAndBg(music: Music) {
        mAlbumCoverView!!.setCoverBitmap(CoverLoader.get().loadRound(music))
        ivPlayingBg!!.setImageBitmap(CoverLoader.get().loadBlur(music))
    }

    private fun setLrc(music: Music) {
        if (music.type == Music.Type.LOCAL) {
            val lrcPath = FileUtils.getLrcFilePath(music)
            if (!TextUtils.isEmpty(lrcPath)) {
                loadLrc(lrcPath)
            } else {
                object : SearchLrc(music.artist, music.title) {
                    override fun onPrepare() {
                        // 设置tag防止歌词下载完成后已切换歌曲
                        mLrcView!!.tag = music
                        loadLrc("")
                        setLrcLabel("正在搜索歌词")
                    }

                    override fun onExecuteSuccess(lrcPath: String?) {
                        if (mLrcView!!.tag !== music) {
                            return
                        }

                        // 清除tag
                        mLrcView!!.tag = null
                        loadLrc(lrcPath)
                        setLrcLabel("暂无歌词")
                    }

                    override fun onExecuteFail(e: Exception?) {
                        if (mLrcView!!.tag !== music) {
                            return
                        }

                        // 清除tag
                        mLrcView!!.tag = null
                        setLrcLabel("暂无歌词")
                    }
                }.execute()
            }
        } else {
            val lrcPath =
                FileUtils.lrcDir + FileUtils.getLrcFileName(music.artist, music.title)
            loadLrc(lrcPath)
        }
    }

    private fun loadLrc(path: String?) {
        val file = File(path)
        mLrcView!!.loadLrc(file)
    }

    private fun setLrcLabel(label: String) {
        mLrcView!!.setLabel(label)
    }

    private fun formatTime(time: Long): String? {
        return SystemUtils.formatTime("mm:ss", time)
    }

    private val mVolumeReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            sbVolume!!.progress = mAudioManager!!.getStreamVolume(AudioManager.STREAM_MUSIC)
        }
    }

    override fun onDestroy() {
        requireContext().unregisterReceiver(mVolumeReceiver)
        AudioPlayer.get().removeOnPlayEventListener(this)
        super.onDestroy()
    }
}