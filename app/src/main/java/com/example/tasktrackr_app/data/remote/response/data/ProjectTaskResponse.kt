package com.example.tasktrackr_app.data.remote.response.data

import com.google.gson.annotations.SerializedName

data class ProjectTaskResponse(
    @SerializedName("title")
    val title: String,

    @SerializedName("description")
    val description: String? = null,

    @SerializedName("status")
    val status: String,

    @SerializedName("start_date")
    val startDate: String? = null,

    @SerializedName("end_date")
    val endDate: String? = null,

    @SerializedName("assignees")
    val assignees: List<TeamMemberData>
)
