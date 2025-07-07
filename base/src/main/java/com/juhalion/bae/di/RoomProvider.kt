package com.juhalion.bae.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import kotlinx.coroutines.Dispatchers

object RoomProvider {
    inline fun <reified T : RoomDatabase> provideDatabase(
        context: Context,
        databaseName: String,
        removeOldData: Boolean,
        migrations: Array<Migration>,
    ): T {
        val roomBuilder =
            Room.databaseBuilder(context = context.applicationContext, klass = T::class.java, name = databaseName)
                .setQueryCoroutineContext(Dispatchers.IO)
        if (removeOldData) roomBuilder.fallbackToDestructiveMigration(true)
        else roomBuilder.addMigrations(*migrations)
        return roomBuilder.build()
    }

    inline fun <reified T : RoomDatabase> provideDatabaseFromAssets(
        context: Context,
        databaseName: String,
        removeOldData: Boolean,
        migrations: Array<Migration>,
    ): T {
        val roomBuilder =
            Room.databaseBuilder(context = context.applicationContext, klass = T::class.java, name = databaseName)
                .createFromAsset("database/$databaseName")
                .setQueryCoroutineContext(Dispatchers.IO)
        if (removeOldData) roomBuilder.fallbackToDestructiveMigration(true)
        else roomBuilder.addMigrations(*migrations)
        return roomBuilder.build()
    }
}