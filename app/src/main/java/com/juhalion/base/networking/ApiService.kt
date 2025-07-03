package com.juhalion.base.networking

import com.juhalion.base.constants.ConstantApi
import com.juhalion.base.models.response.comments.CommentResponse
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {
    @GET(ConstantApi.GET_COMMENT)
    suspend fun getComments(): Response<CommentResponse>
}