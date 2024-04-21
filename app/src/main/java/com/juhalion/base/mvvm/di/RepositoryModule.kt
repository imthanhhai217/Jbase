package com.juhalion.base.mvvm.di

import com.juhalion.base.mvvm.networking.ApiService
import com.juhalion.base.mvvm.repositories.CommentRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import javax.inject.Singleton

@Module
@InstallIn(ActivityComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideCommentRepo(apiService: ApiService): CommentRepo = CommentRepo(apiService)
}