package com.example.tasktrackr_app.data.remote.api

import android.content.Context
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.tasktrackr_app.data.remote.interceptor.TokenInterceptor

object ApiClient {

    private const val BASE_URL = "http://10.0.2.2:8080/api/"

    private fun okHttpClient(context: Context, authApiNoInterceptor: AuthApi): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val tokenInterceptor = TokenInterceptor(context, authApiNoInterceptor)

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(tokenInterceptor)
            .build()
    }

    private fun retrofit(context: Context): Retrofit {
        val defaultClient = OkHttpClient.Builder().build()
        val defaultRetrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(defaultClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val authApiNoInterceptor = defaultRetrofit.create(AuthApi::class.java)

        val clientWithInterceptor = okHttpClient(context, authApiNoInterceptor)

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(clientWithInterceptor)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun authApi(context: Context): AuthApi =
        retrofit(context).create(AuthApi::class.java)

    fun userApi(context: Context): UserApi =
        retrofit(context).create(UserApi::class.java)

    fun teamApi(context: Context): TeamApi =
        retrofit(context).create(TeamApi::class.java)
}
