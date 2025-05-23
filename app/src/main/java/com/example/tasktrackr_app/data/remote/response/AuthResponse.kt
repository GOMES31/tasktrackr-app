package com.example.tasktrackr_app.data.remote.response

import com.google.gson.annotations.SerializedName

data class AuthResponse(
    val status: Int,
    val message: String,
    val data: Data
) {
    data class Data(
        val email: String,

        @SerializedName("access_token")
        val accessToken: String,

        @SerializedName("refresh_token")
        val refreshToken: String
    )
}
