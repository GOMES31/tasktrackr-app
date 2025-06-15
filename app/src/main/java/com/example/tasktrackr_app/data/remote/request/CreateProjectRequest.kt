package com.example.tasktrackr_app.data.remote.request

import com.google.gson.annotations.SerializedName
import java.util.Date

data class CreateProjectRequest(

    val name: String,

    val description: String,

    @SerializedName("team_id")
    val teamId: Long,

    @SerializedName("start_date")
    val startDate: Date?,

    @SerializedName("end_date")
    val endDate: Date?,

    val status: String
)