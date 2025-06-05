package com.example.tasktrackr_app.data.remote.request

import com.google.gson.annotations.SerializedName
import java.util.Date

data class CreateTaskRequest(

    val title: String,

    val description: String,

    @SerializedName("project_id")
    val projectId: Long,

    @SerializedName("end_date")
    val endDate: Date? = null,

    val status: String? = null,

    @SerializedName("assignee_ids")
    val assigneeIds: List<Long>? = null
)