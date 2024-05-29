package com.juhalion.base.mvvm.networking

import com.juhalion.base.mvvm.constants.ConstantApi
import com.juhalion.base.mvvm.models.response.comments.CommentResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET

interface ApiService {
    @GET(ConstantApi.GET_COMMENT)
    suspend fun getComments(): Response<CommentResponse>
}