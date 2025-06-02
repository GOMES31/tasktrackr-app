package com.example.tasktrackr_app.data.remote.response.data

import com.google.gson.annotations.SerializedName

data class TeamMemberData(

    val id: Long,

    val name: String,

    val email: String,

    val role: String
)
