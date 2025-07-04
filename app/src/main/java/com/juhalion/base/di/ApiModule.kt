package com.juhalion.base.di

import com.juhalion.base.api.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    @Provides
    @Singleton
    fun provideApiServices(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }
}