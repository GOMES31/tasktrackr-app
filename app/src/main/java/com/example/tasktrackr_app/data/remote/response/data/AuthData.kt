package com.example.tasktrackr_app.data.remote.response.data

import com.google.gson.annotations.SerializedName

data class AuthData(
        @SerializedName("name")
        val name: String,

        @SerializedName("email")
        val email: String,

        @SerializedName("avatarUrl")
        val avatarUrl: String,

        @SerializedName("access_token")
        val accessToken: String,

        @SerializedName("refresh_token")
        val refreshToken: String
    )

