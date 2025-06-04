package com.example.tasktrackr_app.data.remote.response.data

import com.google.gson.annotations.SerializedName

data class ProjectTaskData(
    val title: String,

    val description: String? = null,

    val status: String,

    @SerializedName("start_date")
    val startDate: String? = null,

    @SerializedName("end_date")
    val endDate: String? = null,

    val assignees: List<TeamMemberData>
)
