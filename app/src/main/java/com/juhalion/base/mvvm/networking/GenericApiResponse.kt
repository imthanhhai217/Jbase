package com.juhalion.base.mvvm.networking

import retrofit2.Response

abstract class GenericApiResponse {

    suspend fun <T> apiCall(call: suspend () -> Response<T>): ApiResponse<T> {
        try {
            val response = call()
            if (response.isSuccessful) {
                val body = response.body()
                body?.let {
                    return ApiResponse.Success(it)
                }
            }
            return ApiResponse.Failed(message = response.message())
        } catch (e: Exception) {
            return ApiResponse.Failed(message = e.message!!)
        }
    }
}