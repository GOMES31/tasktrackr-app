package com.example.tasktrackr_app.data.remote.response.data

import com.google.gson.annotations.SerializedName

data class AuthData(
        val name: String,

        val email: String,

        @SerializedName("avatar_url")
        val avatarUrl: String,

        @SerializedName("access_token")
        val accessToken: String,

        @SerializedName("refresh_token")
        val refreshToken: String
    )


