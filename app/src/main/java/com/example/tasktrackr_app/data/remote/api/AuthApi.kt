package com.example.tasktrackr_app.data.remote.api

import com.example.tasktrackr_app.data.remote.request.SignInRequest
import com.example.tasktrackr_app.data.remote.request.SignUpRequest
import com.example.tasktrackr_app.data.remote.response.AuthResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    @POST("auth/signup")
    suspend fun signUp(@Body body: SignUpRequest): Response<AuthResponse>

    @POST("auth/signin")
    suspend fun signIn(@Body body: SignInRequest): Response<AuthResponse>
}