package me.wcy.music.service.likesong

import android.app.Application
import dagger.Binds
import dagger.Module
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import me.wcy.music.ext.accessEntryPoint

/**
 * Created by wangchenyan.top on 2024/3/21.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class LikeSongProcessorModule {

    @Binds
    abstract fun bindLikeSongProcessor(
        likeSongProcessor: LikeSongProcessorImpl
    ): LikeSongProcessor

    companion object {
        fun Application.audioPlayer(): LikeSongProcessor {
            return accessEntryPoint<LikeSongProcessorEntryPoint>().likeSongProcessor()
        }
    }

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface LikeSongProcessorEntryPoint {
        fun likeSongProcessor(): LikeSongProcessor
    }
}