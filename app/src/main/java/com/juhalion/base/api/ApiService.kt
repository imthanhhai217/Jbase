package com.juhalion.base.api

import com.juhalion.base.constants.ConstantApi
import com.juhalion.base.models.comment.CommentResponse
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {
    @GET(ConstantApi.GET_COMMENT)
    suspend fun getComments(): Response<CommentResponse>
}