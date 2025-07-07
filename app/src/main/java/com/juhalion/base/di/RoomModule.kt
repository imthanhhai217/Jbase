package com.juhalion.base.di

import android.content.Context
import com.juhalion.bae.di.ROOM
import com.juhalion.bae.di.ROOM_FROM_ASSETS
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
    @ROOM
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return RoomProvider.provideDatabase(context = context, databaseName = "jBae.db", removeOldData = true, migrations = emptyArray())
    }

    @Provides
    @Singleton
    @ROOM_FROM_ASSETS
    fun provideDatabaseFromAssets(@ApplicationContext context: Context): AppDatabase {
        return RoomProvider.provideDatabaseFromAssets(context = context, databaseName = "jBae.db", removeOldData = true, migrations = emptyArray())
    }

    @Provides
    @Singleton
    fun provideUserDao(@ROOM database: AppDatabase) = database.userDao()

    @Provides
    @Singleton
    fun provideProductDao(@ROOM database: AppDatabase) = database.productDao()
}