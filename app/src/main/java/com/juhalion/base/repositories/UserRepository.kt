package com.juhalion.base.repositories

import com.juhalion.base.db.dao.UserDAO
import com.juhalion.base.models.comment.user.User
import javax.inject.Inject

class UserRepository @Inject constructor(private val userDao: UserDAO) {

    suspend fun upsertUser(user: User) {
        userDao.upsert(user)
    }

    suspend fun deleteUser(user: User) {
        userDao.delete(user)
    }

    fun getUserByID(id: Int) = userDao.findingUser(id)

    fun getAllUsers() = userDao.fetchListData()
}