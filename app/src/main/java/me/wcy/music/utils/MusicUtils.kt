package me.wcy.music.utils

import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.audiofx.AudioEffect
import android.net.Uri
import android.text.TextUtils

/**
 * 歌曲工具类
 * Created by wcy on 2015/11/27.
 */
object MusicUtils {

    fun getMediaStoreAlbumCoverUri(albumId: Long): Uri {
        val artworkUri = Uri.parse("content://media/external/audio/albumart")
        return ContentUris.withAppendedId(artworkUri, albumId)
    }

    fun isAudioControlPanelAvailable(context: Context): Boolean {
        return isIntentAvailable(
            context,
            Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL)
        )
    }

    private fun isIntentAvailable(context: Context, intent: Intent): Boolean {
        return context.packageManager.resolveActivity(
            intent,
            PackageManager.GET_RESOLVED_FILTER
        ) != null
    }

    fun getArtistAndAlbum(artist: String?, album: String?): String? {
        return if (TextUtils.isEmpty(artist) && TextUtils.isEmpty(album)) {
            ""
        } else if (!TextUtils.isEmpty(artist) && TextUtils.isEmpty(album)) {
            artist
        } else if (TextUtils.isEmpty(artist) && !TextUtils.isEmpty(album)) {
            album
        } else {
            "$artist - $album"
        }
    }
}