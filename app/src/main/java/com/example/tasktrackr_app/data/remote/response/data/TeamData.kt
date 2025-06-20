package com.example.tasktrackr_app.data.remote.response.data

import com.google.gson.annotations.SerializedName

data class TeamData(
    val id: Long,

    val name: String,

    val department: String,

    @SerializedName("image_url")
    val imageUrl: String?,

    val members: List<TeamMemberData>,

    val projects: List<ProjectData>

)
