package com.juhalion.base.mvvm.networking

import android.app.Application
import com.callscreen.caller.basemvvm.R
import com.juhalion.base.mvvm.utils.Utils
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class CustomInterceptor(val application: Application) : Interceptor {
    private lateinit var response: Response
    override fun intercept(chain: Interceptor.Chain): Response {
        if (!Utils.isNetworkConnected(application = application)) {
            throw IOException(application.getString(R.string.no_internet_connection))
        }

        //if need add header params add here
        var request = chain.request().newBuilder().build()
        try {
            response = chain.proceed(request)
        } catch (e: Exception) {
            throw IOException(e.message)
        }
        return response
    }
}