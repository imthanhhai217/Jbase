package com.juhalion.base.api

import com.juhalion.base.constants.ConstantApi
import com.juhalion.base.models.comment.CommentResponse
import com.juhalion.base.models.product.ProductResponse
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {
    @GET(ConstantApi.GET_COMMENT)
    suspend fun getComments(): Response<CommentResponse>


    @GET(ConstantApi.GET_PRODUCT)
    suspend fun getProducts(): Response<ProductResponse>
}