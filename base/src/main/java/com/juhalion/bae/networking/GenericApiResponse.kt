package com.juhalion.bae.networking

import android.util.Log
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException

abstract class GenericApiResponse {
    private val TAG = "GenericApiResponse"

    suspend fun <T> apiCall(call: suspend () -> Response<T>): ApiResponse<T> {
        return try {
            val response = call()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    ApiResponse.Success(code = response.code(), data = body)
                } else {
                    ApiResponse.Failed(code = response.code(), message = "Response body is null")
                }
            } else {
                if (response.code() != 500) {
                    ApiResponse.Failed(code = response.code(), message = response.message())
                } else {
                    ApiResponse.Failed(code = 500, message = "Internal Server Error")
                }
            }
        } catch (e: SocketTimeoutException) {
            Log.e(TAG, "apiCall: Connection timed out", e)
            ApiResponse.Failed(code = 408, // Request Timeout
                message = "Connection to the server was interrupted, please try again!")
        } catch (e: IOException) {
            Log.e(TAG, "apiCall: Network error", e)
            ApiResponse.Failed(code = 502, // Service Unavailable
                message = "Network error, please check your connection!")
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d(TAG, "apiCall: Exception occurred with response = ${e.message}")
            ApiResponse.Failed(code = 500, message = e.message ?: "Unknown error")
        }
    }
}