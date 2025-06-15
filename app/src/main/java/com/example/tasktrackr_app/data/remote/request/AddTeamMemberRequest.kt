package com.example.tasktrackr_app.data.remote.request

import com.google.gson.annotations.SerializedName

data class AddTeamMemberRequest(
    val email: String,
    val role: String,

    @SerializedName("created_at")
    val createdAt: Long
)
