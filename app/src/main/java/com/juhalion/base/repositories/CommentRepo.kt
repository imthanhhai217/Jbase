package com.juhalion.base.repositories

import com.juhalion.bae.networking.ApiResponse
import com.juhalion.bae.networking.GenericApiResponse
import com.juhalion.base.models.comment.CommentResponse
import com.juhalion.base.api.ApiService
import javax.inject.Inject

class CommentRepo @Inject constructor(private val apiService: ApiService) : GenericApiResponse() {

    suspend fun getComments(): ApiResponse<CommentResponse> {
        return apiCall { apiService.getComments() }
    }
}