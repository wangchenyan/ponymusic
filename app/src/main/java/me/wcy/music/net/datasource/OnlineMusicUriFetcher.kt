package me.wcy.music.net.datasource

import android.net.Uri
import kotlinx.coroutines.runBlocking
import me.wcy.music.discover.DiscoverApi
import me.wcy.music.storage.preference.ConfigPreferences
import top.wangchenyan.common.net.apiCall

/**
 * Created by wangchenyan.top on 2024/3/26.
 */
object OnlineMusicUriFetcher {

    fun fetchPlayUrl(uri: Uri): String {
        val songId = uri.getQueryParameter("id")?.toLongOrNull() ?: return uri.toString()
        return runBlocking {
            val res = apiCall {
                DiscoverApi.get()
                    .getSongUrl(songId, ConfigPreferences.playSoundQuality)
            }

            if (res.isSuccessWithData() && res.getDataOrThrow().isNotEmpty()) {
                return@runBlocking res.getDataOrThrow().first().url
            } else {
                return@runBlocking ""
            }
        }
    }
}