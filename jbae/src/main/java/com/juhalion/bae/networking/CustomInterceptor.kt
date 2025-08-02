package com.juhalion.bae.networking

import android.app.Application
import com.juhalion.bae.R
import com.juhalion.bae.utils.JuExtendFunction.getCompatString
import com.juhalion.bae.utils.LoadingStateManager
import com.juhalion.bae.utils.Utils
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject

class CustomInterceptor @Inject constructor(val application: Application) : Interceptor {
    private lateinit var response: Response
    override fun intercept(chain: Interceptor.Chain): Response {
        if (!Utils.isNetworkConnected(application = application)) {
            throw IOException(application.getCompatString(R.string.no_internet_connection))
        }

        // Show loading before the request
        LoadingStateManager.showLoading()

        //if need add header params add here
        var request = chain.request().newBuilder().build()
        try {
            response = chain.proceed(request)
        } catch (e: Exception) {
            throw IOException(e.message)
        } finally {
            // Hide loading after the request, regardless of success or failure
            LoadingStateManager.hideLoading()
        }
        return response
    }
}