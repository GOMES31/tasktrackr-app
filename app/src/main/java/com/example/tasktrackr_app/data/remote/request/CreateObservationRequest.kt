package com.example.tasktrackr_app.data.remote.request

import com.google.gson.annotations.SerializedName

data class CreateObservationRequest(

    @SerializedName("task_id")
    val taskId: Long,

    val message: String,

    @SerializedName("image_url")
    val imageUrl: String?
)