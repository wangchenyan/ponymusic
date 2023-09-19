package me.wcy.music.account.service

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
abstract class UserServiceModule {

    @Binds
    abstract fun bindUserService(userServiceImpl: UserServiceImpl): UserService

    companion object {
        fun Application.userService(): UserService {
            return accessEntryPoint<UserServiceEntryPoint>().userService()
        }
    }

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface UserServiceEntryPoint {
        fun userService(): UserService
    }
}