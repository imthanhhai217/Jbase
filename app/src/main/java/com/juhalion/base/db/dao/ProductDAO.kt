package com.juhalion.base.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.juhalion.bae.db.BaseDAO
import com.juhalion.base.models.product.Product
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDAO : BaseDAO<Product> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    override suspend fun upsert(item: Product)

    @Delete
    override suspend fun delete(item: Product)

    @Query("DELETE FROM products where id = :productID")
    suspend fun delete(productID: Int)

    @Query("SELECT * FROM products")
    override fun fetchListData(): Flow<List<Product>>
}