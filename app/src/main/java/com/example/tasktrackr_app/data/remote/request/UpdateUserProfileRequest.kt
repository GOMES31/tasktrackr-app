package com.example.tasktrackr_app.data.remote.request

data class UpdateUserProfileRequest(
    val name: String?,
    val password: String?,
    val avatarUrl: String?
)
