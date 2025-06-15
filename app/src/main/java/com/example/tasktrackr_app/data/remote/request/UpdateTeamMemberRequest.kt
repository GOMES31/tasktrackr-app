package com.example.tasktrackr_app.data.remote.request

import com.google.gson.annotations.SerializedName

data class UpdateTeamMemberRequest(
    val role: String,

    @SerializedName("updated_at")
    val updatedAt: Long
)
