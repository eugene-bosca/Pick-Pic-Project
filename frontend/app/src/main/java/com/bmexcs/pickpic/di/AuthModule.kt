package com.bmexcs.pickpic.di

import android.content.Context
import com.bmexcs.pickpic.data.repositories.AuthRepository
import com.bmexcs.pickpic.data.sources.AuthDataSource
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideAuthRepository(@ApplicationContext context: Context, authDataSource: AuthDataSource): AuthRepository {
        return AuthRepository(context, authDataSource)
    }
}
