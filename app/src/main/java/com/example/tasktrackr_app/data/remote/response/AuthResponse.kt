package com.example.tasktrackr_app.data.remote.response

data class AuthResponse(
    val email: String,
    val accessToken: String,
    val refreshToken: String
)