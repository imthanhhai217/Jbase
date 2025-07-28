package com.juhalion.bae.db

import kotlinx.coroutines.flow.Flow

interface BaseDAO<T> {
    suspend fun upsert(item: T)
    suspend fun delete(item: T)
    fun getLocalProducts(): Flow<List<T>>
}