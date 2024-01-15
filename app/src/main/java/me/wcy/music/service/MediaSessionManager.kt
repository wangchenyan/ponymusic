package me.wcy.music.service

import android.content.Context
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import me.wcy.music.storage.db.entity.SongEntity
import top.wangchenyan.common.CommonApp
import top.wangchenyan.common.utils.image.ImageUtils

/**
 * Created by hzwangchenyan on 2017/8/8.
 */
class MediaSessionManager(
    private val context: Context,
    private val audioPlayer: AudioPlayer
) {
    private val mediaSession: MediaSessionCompat by lazy {
        MediaSessionCompat(context, TAG).apply {
            setFlags(MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS or MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS)
            setCallback(callback)
            isActive = true
        }
    }
    private var loadCoverJob: Job? = null

    fun updatePlaybackState() {
        val state = if (audioPlayer.playState.value.isPlaying
            || audioPlayer.playState.value.isPreparing
        ) {
            PlaybackStateCompat.STATE_PLAYING
        } else {
            PlaybackStateCompat.STATE_PAUSED
        }
        mediaSession.setPlaybackState(
            PlaybackStateCompat.Builder()
                .setActions(MEDIA_SESSION_ACTIONS)
                .setState(state, audioPlayer.getAudioPosition(), 1f)
                .build()
        )
    }

    fun updateMetaData(song: SongEntity?) {
        loadCoverJob?.cancel()
        if (song == null) {
            mediaSession.setMetadata(null)
            return
        } else {
            val builder = MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, song.title)
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, song.artist)
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, song.album)
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ARTIST, song.artist)
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, song.duration)
                .putLong(
                    MediaMetadataCompat.METADATA_KEY_NUM_TRACKS,
                    (audioPlayer.playlist.value?.size ?: 0).toLong()
                )
            mediaSession.setMetadata(builder.build())
            loadCoverJob = CommonApp.appScope.launch {
                val bitmap = ImageUtils.loadBitmap(song.getLargeCover()).data
                if (bitmap != null) {
                    builder.putBitmap(
                        MediaMetadataCompat.METADATA_KEY_ALBUM_ART,
                        bitmap
                    )
                    mediaSession.setMetadata(builder.build())
                }
            }
        }
    }

    fun getMediaSession(): MediaSessionCompat.Token {
        return mediaSession.sessionToken
    }

    private val callback: MediaSessionCompat.Callback = object : MediaSessionCompat.Callback() {
        override fun onPlay() {
            audioPlayer.startPlayer()
        }

        override fun onPause() {
            audioPlayer.pausePlayer()
        }

        override fun onSkipToNext() {
            audioPlayer.next()
        }

        override fun onSkipToPrevious() {
            audioPlayer.prev()
        }

        override fun onStop() {
            audioPlayer.stopPlayer()
        }

        override fun onSeekTo(pos: Long) {
            audioPlayer.seekTo(pos.toInt())
        }
    }

    companion object {
        private const val TAG = "MediaSessionManager"
        private const val MEDIA_SESSION_ACTIONS = (PlaybackStateCompat.ACTION_PLAY
                or PlaybackStateCompat.ACTION_PAUSE
                or PlaybackStateCompat.ACTION_PLAY_PAUSE
                or PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                or PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
                or PlaybackStateCompat.ACTION_STOP
                or PlaybackStateCompat.ACTION_SEEK_TO)
    }
}