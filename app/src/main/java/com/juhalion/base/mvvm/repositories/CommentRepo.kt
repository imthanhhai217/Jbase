package com.juhalion.base.mvvm.repositories

import com.juhalion.base.mvvm.models.response.comments.CommentResponse
import com.juhalion.base.mvvm.networking.ApiResponse
import com.juhalion.base.mvvm.networking.ApiService
import com.juhalion.base.mvvm.networking.GenericApiResponse
import javax.inject.Inject

class CommentRepo @Inject constructor(private val apiService: ApiService): GenericApiResponse() {

    suspend fun getComments():ApiResponse<CommentResponse>{
        return apiCall { apiService.getComments() }
    }
}