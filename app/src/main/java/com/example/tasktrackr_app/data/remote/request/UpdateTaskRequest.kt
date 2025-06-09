package com.example.tasktrackr_app.data.remote.request

import com.google.gson.annotations.SerializedName
import java.util.Date

data class UpdateTaskRequest(

    val id: Long,

    val title: String?,

    val description: String?,

    val status: String?,

    @SerializedName("start_date")
    val startDate: Date?,

    @SerializedName("end_date")
    val endDate: Date?,

    @SerializedName("assignee_ids")
    val assigneeIds: List<Long>?
)