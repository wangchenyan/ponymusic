package me.wcy.music.service

import android.app.Application
import dagger.Binds
import dagger.Module
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import me.wcy.music.ext.accessEntryPoint

/**
 * Created by wangchenyan.top on 2023/9/18.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class AudioPlayerModule {

    @Binds
    abstract fun bindAudioPlayer(
        audioPlayerImpl: AudioPlayerImpl
    ): IAudioPlayer

    companion object {
        fun Application.audioPlayer(): IAudioPlayer {
            return accessEntryPoint<AudioPlayerEntryPoint>().audioPlayer()
        }
    }

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface AudioPlayerEntryPoint {
        fun audioPlayer(): IAudioPlayer
    }
}