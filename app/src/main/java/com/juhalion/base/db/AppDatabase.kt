package com.juhalion.base.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.juhalion.base.db.dao.ProductDAO
import com.juhalion.base.db.dao.UserDAO
import com.juhalion.base.models.product.Product
import com.juhalion.base.models.user.User

@Database(entities = [User::class, Product::class], version = 2, exportSchema = true)
@TypeConverters(RoomConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDAO
    abstract fun productDao(): ProductDAO
}