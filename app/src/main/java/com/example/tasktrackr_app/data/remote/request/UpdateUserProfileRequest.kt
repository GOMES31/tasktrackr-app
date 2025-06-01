package com.example.tasktrackr_app.data.remote.request

import com.google.gson.annotations.SerializedName

data class UpdateUserProfileRequest(
    val name: String?,
    val password: String?,

    @SerializedName("avatar_url")
    val avatarUrl: String?
)
