package com.juhalion.base.di

import android.content.Context
import com.juhalion.bae.di.RoomProvider
import com.juhalion.base.db.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return RoomProvider.provideDatabase(context = context, databaseName = "jBae.db", removeOldData = false, migrations = emptyArray())
    }

    @Provides
    @Singleton
    fun provideUserDao(database: AppDatabase) = database.userDao()
}