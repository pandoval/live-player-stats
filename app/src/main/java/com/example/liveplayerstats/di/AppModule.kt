package com.example.liveplayerstats.di

import android.app.Application
import android.content.Context
import com.example.liveplayerstats.NBAApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit =
        Retrofit.Builder()
            .baseUrl(NBAApi.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideNBAApi(retrofit: Retrofit): NBAApi =
        retrofit.create(NBAApi::class.java)

    @Provides
    @Singleton
    fun provideContext(application: Application): Context =
        application.applicationContext

}