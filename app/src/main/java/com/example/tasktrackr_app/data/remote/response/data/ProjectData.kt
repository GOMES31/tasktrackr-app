package com.example.tasktrackr_app.data.remote.response.data

import com.google.gson.annotations.SerializedName
import java.util.Date

data class ProjectData(

    val id: Long,

    val name: String,

    val description: String? = null,

    @SerializedName("start_date")
    val startDate: Date? = null,

    @SerializedName("end_date")
    val endDate: Date? = null,

    val status: String,

    @SerializedName("created_at")
    val createdAt: Date? = null,

    @SerializedName("updated_at")
    val updatedAt: Date? = null,

    val team: TeamInfo,

    val tasks: List<TaskData>
) {
    data class TeamInfo(
        val id: Long,
        val name: String
    )
}