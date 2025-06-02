package com.example.tasktrackr_app.data.remote.response.data

import com.google.gson.annotations.SerializedName

data class ProjectData(
    val title: String,

    val description: String? = null,

    @SerializedName("start_date")
    val startDate: String? = null,

    @SerializedName("end_date")
    val endDate: String? = null,

    val status: String,

    val tasks: List<ProjectTaskData>
)
