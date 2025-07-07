package com.juhalion.base.repositories

import com.juhalion.base.db.dao.UserDAO
import com.juhalion.base.models.user.User
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(private val userDao: UserDAO) {

    suspend fun upsertUser(user: User) {
        userDao.upsert(user)
    }

    suspend fun deleteUser(user: User) {
        userDao.delete(user)
    }

    suspend fun deleteUser(userID: Int) {
        userDao.delete(userID)
    }

    fun getUserByID(id: Int) = userDao.findingUser(id)

    fun getAllUsers() = userDao.fetchListData()
}