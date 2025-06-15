package com.example.tasktrackr_app.data.remote.response.data

import com.google.gson.annotations.SerializedName

data class UserProfileData(
    val name: String,
    val email: String,
    @SerializedName("avatar_url")
    val avatarUrl: String?
)
