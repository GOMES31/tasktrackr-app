package com.example.tasktrackr_app.data.remote.response.data

import com.google.gson.annotations.SerializedName

data class UserTeamsData(
    @SerializedName("name")
    val name: String,

    @SerializedName("department")
    val department: String,

    @SerializedName("image_url")
    val imageUrl: String?,

    @SerializedName("projects")
    val projects: List<ProjectData>,

    @SerializedName("members")
    val members: List<TeamMemberData>
)
