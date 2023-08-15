package me.wcy.music.service

import android.os.Build
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import me.wcy.music.application.AppCache
import me.wcy.music.model.Music
import me.wcy.music.utils.CoverLoader

/**
 * Created by hzwangchenyan on 2017/8/8.
 */
class MediaSessionManager private constructor() {
    private var playService: PlayService? = null
    private var mediaSession: MediaSessionCompat? = null

    private object SingletonHolder {
        val instance = MediaSessionManager()
    }

    fun init(playService: PlayService?) {
        this.playService = playService
        setupMediaSession()
    }

    private fun setupMediaSession() {
        mediaSession = MediaSessionCompat(playService!!, TAG)
        mediaSession!!.setFlags(MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS or MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS)
        mediaSession!!.setCallback(callback)
        mediaSession!!.isActive = true
    }

    fun updatePlaybackState() {
        val state = if (AudioPlayer.get().isPlaying || AudioPlayer.get()
                .isPreparing
        ) PlaybackStateCompat.STATE_PLAYING else PlaybackStateCompat.STATE_PAUSED
        mediaSession!!.setPlaybackState(
            PlaybackStateCompat.Builder()
                .setActions(MEDIA_SESSION_ACTIONS)
                .setState(state, AudioPlayer.get().audioPosition, 1f)
                .build()
        )
    }

    fun updateMetaData(music: Music?) {
        if (music == null) {
            mediaSession!!.setMetadata(null)
            return
        }
        val metaData = MediaMetadataCompat.Builder()
            .putString(MediaMetadataCompat.METADATA_KEY_TITLE, music.title)
            .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, music.artist)
            .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, music.album)
            .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ARTIST, music.artist)
            .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, music.duration)
            .putBitmap(
                MediaMetadataCompat.METADATA_KEY_ALBUM_ART,
                CoverLoader.get().loadThumb(music)
            )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            metaData.putLong(
                MediaMetadataCompat.METADATA_KEY_NUM_TRACKS,
                AppCache.get().getLocalMusicList().size.toLong()
            )
        }
        mediaSession!!.setMetadata(metaData.build())
    }

    private val callback: MediaSessionCompat.Callback = object : MediaSessionCompat.Callback() {
        override fun onPlay() {
            AudioPlayer.get().playPause()
        }

        override fun onPause() {
            AudioPlayer.get().playPause()
        }

        override fun onSkipToNext() {
            AudioPlayer.get().next()
        }

        override fun onSkipToPrevious() {
            AudioPlayer.get().prev()
        }

        override fun onStop() {
            AudioPlayer.get().stopPlayer()
        }

        override fun onSeekTo(pos: Long) {
            AudioPlayer.get().seekTo(pos.toInt())
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

        fun get(): MediaSessionManager {
            return SingletonHolder.instance
        }
    }
}