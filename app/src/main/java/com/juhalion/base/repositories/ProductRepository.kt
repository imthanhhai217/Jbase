package com.juhalion.base.repositories

import com.juhalion.base.db.dao.ProductDAO
import com.juhalion.base.models.product.Product
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepository @Inject constructor(private val productDao: ProductDAO) {
    suspend fun upsertProduct(product: Product) {
        productDao.upsert(product)
    }

    suspend fun deleteProduct(product: Product) {
        productDao.delete(product)
    }

    suspend fun deleteProduct(productID: Int) {
        productDao.delete(productID)
    }

    fun getAllProducts() = productDao.fetchListData()
}