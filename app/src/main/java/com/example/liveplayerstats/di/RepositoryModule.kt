package com.example.liveplayerstats.di

import android.content.Context
import com.example.liveplayerstats.NBAApi
import com.example.liveplayerstats.newplayercomponents.NewPlayerRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object RepositoryModule {

    @Singleton
    @Provides
    fun provideNewPlayerRepository(
        nbaApi: NBAApi
    ): NewPlayerRepository {
        return NewPlayerRepository(nbaApi)
    }
}