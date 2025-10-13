package me.wcy.music.appwidget

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Created by wangchenyan.top on 2025/10/1.
 */

class PlayActionCallback : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        withContext(Dispatchers.Main) {
            WidgetRepository.getMediaController()?.play()
        }
    }
}

class PauseActionCallback : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        withContext(Dispatchers.Main) {
            WidgetRepository.getMediaController()?.pause()
        }
    }
}

class PrevActionCallback : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        withContext(Dispatchers.Main) {
            WidgetRepository.getMediaController()?.seekToPreviousMediaItem()
            WidgetRepository.getMediaController()?.play()
        }
    }
}

class NextActionCallback : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        withContext(Dispatchers.Main) {
            WidgetRepository.getMediaController()?.seekToNextMediaItem()
            WidgetRepository.getMediaController()?.play()
        }
    }
}