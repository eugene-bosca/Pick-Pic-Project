package com.bmexcs.pickpic.di

import com.bmexcs.pickpic.data.sources.AuthDataSource
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
    fun provideUserDataSource(authDataSource: AuthDataSource): UserDataSource {
        return UserDataSource(authDataSource)
    }
}
