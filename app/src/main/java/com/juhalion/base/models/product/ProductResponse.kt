package com.juhalion.base.models.product


import com.google.gson.annotations.SerializedName

data class ProductResponse(
    @SerializedName("limit")
    val limit: Int,
    @SerializedName("products")
    val products: List<ProductX>,
    @SerializedName("skip")
    val skip: Int,
    @SerializedName("total")
    val total: Int
)