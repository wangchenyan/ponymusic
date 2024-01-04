package me.wcy.music.storage.db

import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import top.wangchenyan.common.CommonApp

/**
 * Created by wangchenyan.top on 2023/7/20.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    fun provideAppDatabase(): MusicDatabase {
        return Room.databaseBuilder(
            CommonApp.app,
            MusicDatabase::class.java,
            "music_db"
        ).build()
    }
}