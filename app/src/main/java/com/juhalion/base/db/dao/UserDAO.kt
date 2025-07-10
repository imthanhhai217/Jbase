package com.juhalion.base.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.juhalion.bae.db.BaseDAO
import com.juhalion.base.models.user.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDAO : BaseDAO<User> {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    override suspend fun upsert(item: User)

    @Delete
    override suspend fun delete(item: User)

    @Query("DELETE FROM users where id = :userID")
    suspend fun delete(userID: Int)

    @Query("SELECT * FROM users")
    override fun fetchListData(): Flow<List<User>>

    @Query("SELECT * FROM users where id = :id")
    fun findingUser(id: Int): Flow<User?>
}