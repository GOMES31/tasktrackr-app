package com.example.tasktrackr_app.data.remote.response.data

import com.google.gson.annotations.SerializedName
import java.util.Date
import com.example.tasktrackr_app.data.remote.response.data.ObservationData

data class TaskData(
    val id: Long,
    val title: String,
    val description: String? = null,
    val status: String,

    @SerializedName("start_date")
    val startDate: Date? = null,

    @SerializedName("end_date")
    val endDate: Date? = null,

    @SerializedName("created_at")
    val createdAt: Date? = null,

    @SerializedName("updated_at")
    val updatedAt: Date? = null,

    val project: ProjectInfo,
    val assignees: List<TeamMemberData>,
    val observations: List<ObservationData>? = null
) {
    data class ProjectInfo(
        val id: Long,
        val name: String,
        val description: String? = null,
        val status: String? = null
    )

}