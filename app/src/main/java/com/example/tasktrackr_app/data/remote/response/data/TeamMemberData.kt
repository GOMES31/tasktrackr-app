package com.example.tasktrackr_app.data.remote.response.data

import com.google.gson.annotations.SerializedName

data class TeamMemberData(
    @SerializedName("name")
    val name: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("role")
    val role: String
)
