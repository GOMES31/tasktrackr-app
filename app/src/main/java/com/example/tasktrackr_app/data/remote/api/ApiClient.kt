package com.example.tasktrackr_app.data.remote.api

import android.content.Context
import com.example.tasktrackr_app.data.local.AuthRepository
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    // Localhost
    private const val BASE_URL = "http://10.0.2.2:8080/api/"

    private fun okHttpClient(context: Context): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        // auth interceptor
        val authInterceptor = Interceptor { chain ->
            val repo = AuthRepository(context)
            val original = chain.request()
            val builder  = original.newBuilder()
            repo.getAccessToken()?.let { token ->
                builder.header("Authorization", "Bearer $token")
            }
            chain.proceed(builder.build())
        }

        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor(authInterceptor)
            .build()
    }

    private fun retrofit(context: Context): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient(context))
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    fun authApi(context: Context): AuthApi =
        retrofit(context).create(AuthApi::class.java)
}
