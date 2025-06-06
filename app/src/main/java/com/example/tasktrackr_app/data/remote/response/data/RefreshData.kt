package com.example.tasktrackr_app.data.remote.response.data

import com.google.gson.annotations.SerializedName

data class RefreshData (
    @SerializedName("access_token")
    val accessToken: String,

    @SerializedName("refresh_token")
    val refreshToken: String
)