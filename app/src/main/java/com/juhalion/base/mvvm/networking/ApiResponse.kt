package com.juhalion.base.mvvm.networking

sealed class ApiResponse<T>(
    val data: T? = null,
    val message: String? = null,
) {
    class Success<T>(data: T) : ApiResponse<T>(data)
    class Failed<T>(data: T? = null, message: String) : ApiResponse<T>(data, message)
    class Loading<T> : ApiResponse<T>()
}