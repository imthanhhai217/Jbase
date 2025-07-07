package com.juhalion.base.models.product

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,

    @ColumnInfo(name = "name") val name: String,

    @ColumnInfo(name = "price") val price: Double,

    @ColumnInfo(name = "in_stock") val inStock: Boolean = true,

    @ColumnInfo(name = "image") val image: List<String> = emptyList(),

    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis()
)