package me.wcy.music.appwidget

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import androidx.datastore.preferences.preferencesDataStore
import androidx.media3.common.MediaItem
import androidx.media3.session.MediaController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import me.wcy.music.R
import me.wcy.music.service.PlayServiceModule.playerController
import me.wcy.music.service.PlayState
import me.wcy.music.service.PlayerController
import me.wcy.music.utils.BitmapUtils.blur
import me.wcy.music.utils.toSongEntity
import top.wangchenyan.common.utils.image.ImageUtils

/**
 * Created by wangchenyan.top on 2025/9/30.
 */
object WidgetRepository : CoroutineScope by MainScope() {
    val Context.widgetStore by preferencesDataStore("MusicAppWidget")
    private lateinit var playerController: PlayerController
    private lateinit var state: WidgetState

    private val _coverBitmapFlow: MutableStateFlow<Bitmap?> = MutableStateFlow(null)
    val coverBitmapFlow: StateFlow<Bitmap?> = _coverBitmapFlow

    private val _bgBitmapFlow: MutableStateFlow<Bitmap?> = MutableStateFlow(null)
    val bgBitmapFlow: StateFlow<Bitmap?> = _bgBitmapFlow

    private var loadCoverJob: Job? = null

    fun init(application: Application) {
        if (::playerController.isInitialized) {
            return
        }
        playerController = application.playerController()
        state = WidgetState()
            .copy(application, playerController.currentSong.value)
            .copy(playerController.playState.value)
        playerController.currentSong.observeForever {
            val newState = state.copy(application, it)
            if (state != newState) {
                state = newState
                loadCoverJob?.cancel()
                launch {
                    _coverBitmapFlow.emit(null)
                    _bgBitmapFlow.emit(null)
                    MusicAppWidget().updateState(application, state)
                }
                loadCoverJob = launch {
                    val result = ImageUtils.loadBitmap(state.album)
                    if (result.isSuccessWithData()) {
                        val bitmap = result.getDataOrThrow()
                        val bgBitmap = bitmap.blur(application)
                        _coverBitmapFlow.emit(bitmap)
                        _bgBitmapFlow.emit(bgBitmap)
                        MusicAppWidget().updateState(application, state)
                    }
                }
            }
        }
        launch {
            playerController.playState.collectLatest {
                val newState = state.copy(it)
                if (state != newState) {
                    state = newState
                    MusicAppWidget().updateState(application, state)
                }
            }
        }
    }

    fun getMediaController(): MediaController? {
        if (::playerController.isInitialized.not()) {
            return null
        }
        return playerController.mediaController
    }

    private fun WidgetState.copy(context: Context, mediaItem: MediaItem?): WidgetState {
        val song = mediaItem?.toSongEntity()
        return copy(
            title = song?.title.orEmpty().ifEmpty { context.getString(R.string.no_playing_song) },
            artist = song?.artist.orEmpty(),
            album = song?.getSmallCover().orEmpty()
        )
    }

    private fun WidgetState.copy(playState: PlayState): WidgetState {
        return copy(
            isPlaying = (playState == PlayState.Playing)
        )
    }
}