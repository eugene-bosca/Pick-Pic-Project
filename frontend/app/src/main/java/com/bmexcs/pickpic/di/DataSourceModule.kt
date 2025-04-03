package com.bmexcs.pickpic.di

import com.bmexcs.pickpic.data.sources.EventDataSource
import com.bmexcs.pickpic.data.sources.ImageDataSource
import com.bmexcs.pickpic.data.sources.UserDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataSourceModule {

    @Provides
    @Singleton
    fun provideUserDataSource(): UserDataSource {
        return UserDataSource()
    }

    @Provides
    @Singleton
    fun provideEventDataSource(): EventDataSource {
        return EventDataSource()
    }

    @Provides
    @Singleton
    fun provideImageDataSource(): ImageDataSource {
        return ImageDataSource()
    }
}
