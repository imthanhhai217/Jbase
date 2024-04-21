package com.juhalion.base.mvvm.di

import android.app.Application
import com.callscreen.caller.basemvvm.BuildConfig
import com.juhalion.base.mvvm.constants.ConstantApi
import com.juhalion.base.mvvm.networking.ApiService
import com.juhalion.base.mvvm.networking.CustomInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideBaseUrl() = ConstantApi.BASE_URL

    @Provides
    @Singleton
    fun provideInterceptor(application: Application): CustomInterceptor {
        return CustomInterceptor(application)
    }

    @Provides
    @Singleton
    fun provideOkHttp(customInterceptor: CustomInterceptor): OkHttpClient {
        val logs = HttpLoggingInterceptor().apply {
            if (BuildConfig.DEBUG) {
                level = HttpLoggingInterceptor.Level.BODY
            } else {
                level = HttpLoggingInterceptor.Level.NONE
            }
        }
        return OkHttpClient.Builder().addInterceptor(customInterceptor).addInterceptor(logs)
            .readTimeout(60, TimeUnit.SECONDS).writeTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS).build()
    }

    @Provides
    @Singleton
    fun provideGsonConverterFactory():GsonConverterFactory = GsonConverterFactory.create()

    @Provides
    @Singleton
    fun provideRetrofit(
        baseUrl: String,
        client: OkHttpClient,
        gsonConverterFactory: GsonConverterFactory,
    ): Retrofit {
        return Retrofit.Builder().baseUrl(baseUrl).client(client)
            .addConverterFactory(gsonConverterFactory).build()
    }

    @Provides
    @Singleton
    fun provideApiServices(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }
}