package me.wcy.music.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.audiofx.AudioEffect
import android.text.TextUtils
import androidx.core.text.buildSpannedString
import com.blankj.utilcode.util.IntentUtils
import me.wcy.music.R
import me.wcy.music.service.MusicService.Companion.EXTRA_NOTIFICATION
import top.wangchenyan.common.ext.getColorEx
import top.wangchenyan.common.widget.CustomSpan.appendStyle

/**
 * 歌曲工具类
 * Created by wcy on 2015/11/27.
 */
object MusicUtils {

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

    fun keywordsTint(context: Context, text: String, keywords: String): CharSequence {
        if (text.isEmpty() || keywords.isEmpty()) {
            return text
        }
        val splitText = text.split(keywords)
        return buildSpannedString {
            splitText.forEachIndexed { index, s ->
                append(s)
                if (index < splitText.size - 1) {
                    appendStyle(
                        keywords,
                        color = context.getColorEx(R.color.common_theme_color)
                    )
                }
            }
        }
    }

    fun String.asSmallCover(): String {
        return appendImageSize(200)
    }

    fun String.asLargeCover(): String {
        return appendImageSize(800)
    }

    fun getStartPlayingPageIntent(context: Context): Intent {
        return IntentUtils.getLaunchAppIntent(context.packageName).apply {
            putExtra(EXTRA_NOTIFICATION, true)
            action = Intent.ACTION_VIEW
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }

    private fun String.appendImageSize(size: Int): String {
        return if (contains("?")) {
            "$this&param=${size}y${size}"
        } else {
            "$this?param=${size}y${size}"
        }
    }
}