package com.juhalion.base.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.juhalion.base.db.dao.UserDAO
import com.juhalion.base.models.comment.user.User

@Database(entities = [User::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDAO
}