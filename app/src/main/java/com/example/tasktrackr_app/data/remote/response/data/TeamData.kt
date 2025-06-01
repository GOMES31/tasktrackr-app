package com.example.tasktrackr_app.data.remote.response.data

import com.google.gson.annotations.SerializedName

data class TeamData(
    val name: String,

    val department: String,

    @SerializedName("image_url")
    val imageUrl: String?,

    val projects: List<ProjectData>,

    val members: List<TeamMemberData>
)
