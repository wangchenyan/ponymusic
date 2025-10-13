package me.wcy.music.appwidget

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.updateAll
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.ContentScale
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.first
import me.wcy.music.R
import me.wcy.music.appwidget.WidgetRepository.widgetStore
import me.wcy.music.utils.MusicUtils

/**
 * Created by wangchenyan.top on 2025/9/29.
 */
class MusicAppWidget : GlanceAppWidget(), CoroutineScope by MainScope() {

    @OptIn(ExperimentalUnitApi::class)
    @SuppressLint("RestrictedApi")
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        Log.i(TAG, "provideGlance")
        val store = context.widgetStore
        val initial = store.data.first()
        provideContent {
            val data by store.data.collectAsState(initial)
            val coverBitmap by WidgetRepository.coverBitmapFlow.collectAsState()
            val bgBitmap by WidgetRepository.bgBitmapFlow.collectAsState()
            Box(
                modifier = GlanceModifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = GlanceModifier.fillMaxWidth()
                        .height(166.dp)
                        .cornerRadius(20.dp),
                    contentAlignment = Alignment.TopEnd
                ) {
                    Image(
                        modifier = GlanceModifier.fillMaxSize(),
                        provider = if (bgBitmap != null) {
                            ImageProvider(bgBitmap!!)
                        } else {
                            ImageProvider(R.drawable.bg_playing_default)
                        },
                        contentScale = ContentScale.Crop,
                        contentDescription = "bg"
                    )

                    Row(
                        modifier = GlanceModifier
                            .fillMaxSize()
                            .padding(start = 20.dp, end = 24.dp)
                            .background(Color(R.color.translucent_black_p30))
                            .clickable(
                                actionStartActivity(MusicUtils.getStartPlayingPageIntent(context))
                            ),
                        verticalAlignment = Alignment.Vertical.CenterVertically
                    ) {
                        Box(
                            modifier = GlanceModifier.size(118.dp)
                                .background(R.color.translucent_white_p10)
                                .cornerRadius(100.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                modifier = GlanceModifier.size(112.dp),
                                provider = ImageProvider(R.drawable.bg_playing_disc_widget),
                                contentDescription = "Disc"
                            )

                            Image(
                                modifier = GlanceModifier.size(74.dp).cornerRadius(100.dp),
                                provider = if (coverBitmap != null) {
                                    ImageProvider(coverBitmap!!)
                                } else {
                                    ImageProvider(R.drawable.bg_playing_default_cover)
                                },
                                contentDescription = "Cover"
                            )
                        }

                        Column(
                            modifier = GlanceModifier.fillMaxWidth()
                                .height(118.dp)
                                .padding(horizontal = 16.dp)
                        ) {
                            Text(
                                text = data[Name] ?: context.getString(R.string.no_playing_song),
                                modifier = GlanceModifier.fillMaxWidth(),
                                style = TextStyle(
                                    color = ColorProvider(R.color.white),
                                    fontSize = TextUnit(18f, TextUnitType.Sp),
                                    fontWeight = FontWeight.Bold
                                ),
                                maxLines = 2
                            )
                            Row(modifier = GlanceModifier.fillMaxWidth().height(6.dp)) {}
                            Text(
                                text = data[Artist] ?: "",
                                modifier = GlanceModifier.fillMaxWidth(),
                                style = TextStyle(
                                    color = ColorProvider(R.color.grey_300),
                                    fontSize = TextUnit(14f, TextUnitType.Sp),
                                ),
                                maxLines = 1
                            )
                            Box(
                                modifier = GlanceModifier.fillMaxSize(),
                                contentAlignment = Alignment.BottomStart
                            ) {
                                Row(
                                    modifier = GlanceModifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.Vertical.CenterVertically
                                ) {
                                    Image(
                                        modifier = GlanceModifier.size(20.dp)
                                            .clickable(actionRunCallback<PrevActionCallback>()),
                                        provider = ImageProvider(R.drawable.ic_previous),
                                        contentDescription = "Prev"
                                    )
                                    Row(modifier = GlanceModifier.width(20.dp)) { }
                                    Image(
                                        modifier = GlanceModifier.size(36.dp).clickable(
                                            if (data[IsPlaying] == true) {
                                                actionRunCallback<PauseActionCallback>()
                                            } else {
                                                actionRunCallback<PlayActionCallback>()
                                            }
                                        ),
                                        provider = ImageProvider(
                                            if (data[IsPlaying] == true) {
                                                R.drawable.ic_pause
                                            } else {
                                                R.drawable.ic_play
                                            }
                                        ),
                                        contentDescription = "PlayPause"
                                    )
                                    Row(modifier = GlanceModifier.width(20.dp)) { }
                                    Image(
                                        modifier = GlanceModifier.size(20.dp)
                                            .clickable(actionRunCallback<NextActionCallback>()),
                                        provider = ImageProvider(R.drawable.ic_next),
                                        contentDescription = "Next"
                                    )
                                }
                            }
                        }
                    }

                    Image(
                        modifier = GlanceModifier.size(40.dp).padding(10.dp),
                        provider = ImageProvider(R.drawable.ic_launcher_round),
                        contentDescription = "Logo"
                    )
                }
            }
        }
    }

    suspend fun updateState(context: Context, state: WidgetState) {
        context.widgetStore.updateData {
            it.toMutablePreferences().apply {
                set(Name, state.title)
                set(Artist, state.artist)
                set(IsPlaying, state.isPlaying)
            }
        }
        updateAll(context)
    }

    companion object {
        private const val TAG = "MusicAppWidget"
        private val Name = stringPreferencesKey("title")
        private val Artist = stringPreferencesKey("artist")
        private val IsPlaying = booleanPreferencesKey("is_playing")
    }
}