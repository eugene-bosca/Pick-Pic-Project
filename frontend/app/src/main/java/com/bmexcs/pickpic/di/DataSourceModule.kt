package com.bmexcs.pickpic.di

import CacheImageProxy
import com.bmexcs.pickpic.data.sources.CacheImageDataSource
import com.bmexcs.pickpic.data.sources.EventDataSource
import com.bmexcs.pickpic.data.sources.ImageDataSource
import com.bmexcs.pickpic.data.sources.RealDataSource
import com.bmexcs.pickpic.data.sources.RealImageDataSource
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
    @RealDataSource
    fun provideRealImageDataSource(): ImageDataSource {
        return RealImageDataSource() // Real implementation
    }

    @Provides
    @Singleton
    @CacheImageDataSource
    fun provideImageDataSource(
        @RealDataSource realImageDataSource: ImageDataSource
    ): ImageDataSource {
        return CacheImageProxy(realImageDataSource) // Proxy wraps the real data source
    }
}
