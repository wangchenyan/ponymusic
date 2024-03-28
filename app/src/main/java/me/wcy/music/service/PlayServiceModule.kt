package me.wcy.music.service

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.media3.common.Player
import dagger.Module
import dagger.Provides
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import me.wcy.music.ext.accessEntryPoint
import me.wcy.music.storage.db.MusicDatabase
import top.wangchenyan.common.ext.toUnMutable

/**
 * Created by wangchenyan.top on 2024/3/26.
 */
@Module
@InstallIn(SingletonComponent::class)
object PlayServiceModule {
    private var player: Player? = null
    private var playerController: PlayerController? = null

    private val _isPlayerReady = MutableLiveData(false)
    val isPlayerReady = _isPlayerReady.toUnMutable()

    fun setPlayer(player: Player) {
        this.player = player
        _isPlayerReady.value = true
    }

    @Provides
    fun providerPlayerController(db: MusicDatabase): PlayerController {
        return playerController ?: run {
            val player = player ?: throw IllegalStateException("Player not prepared!")
            PlayerControllerImpl(player, db).also {
                playerController = it
            }
        }
    }

    fun Application.playerController(): PlayerController {
        return accessEntryPoint<PlayerControllerEntryPoint>().playerController()
    }

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface PlayerControllerEntryPoint {
        fun playerController(): PlayerController
    }
}
