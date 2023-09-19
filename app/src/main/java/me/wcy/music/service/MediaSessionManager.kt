package me.wcy.music.service

import android.content.Context
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import me.wcy.music.storage.db.entity.SongEntity

/**
 * Created by hzwangchenyan on 2017/8/8.
 */
class MediaSessionManager(
    private val context: Context,
    private val audioPlayer: IAudioPlayer
) {
    private val mediaSession: MediaSessionCompat by lazy {
        MediaSessionCompat(context, TAG).apply {
            setFlags(MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS or MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS)
            setCallback(callback)
            isActive = true
        }
    }

    fun updatePlaybackState() {
        val state = if (audioPlayer.playState.value.isPlaying) {
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

    fun updateMetaData(music: SongEntity?) {
        if (music == null) {
            mediaSession.setMetadata(null)
            return
        }
        mediaSession.setMetadata(
            MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, music.title)
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, music.artist)
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, music.album)
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ARTIST, music.artist)
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, music.duration)
                // TODO
                // .putBitmap(
                //     MediaMetadataCompat.METADATA_KEY_ALBUM_ART,
                //     CoverLoader.get().loadThumb(music)
                // )
                .putLong(
                    MediaMetadataCompat.METADATA_KEY_NUM_TRACKS,
                    (audioPlayer.playlist.value?.size ?: 0).toLong()
                )
                .build()
        )
    }

    private val callback: MediaSessionCompat.Callback = object : MediaSessionCompat.Callback() {
        override fun onPlay() {
            audioPlayer.playPause()
        }

        override fun onPause() {
            audioPlayer.playPause()
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