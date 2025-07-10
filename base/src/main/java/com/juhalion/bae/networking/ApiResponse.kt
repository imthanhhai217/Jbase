package com.juhalion.bae.networking

sealed class ApiResponse<T>(
        val code: Int? = null,
        val data: T? = null,
        val message: String? = null,
) {
    class Success<T>(code: Int, data: T) : ApiResponse<T>(code = code, data = data)
    class Failed<T>(code: Int, data: T? = null, message: String) :
        ApiResponse<T>(code = code, data = data, message = message)

    class Loading<T> : ApiResponse<T>()
}