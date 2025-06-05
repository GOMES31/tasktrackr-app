package com.example.tasktrackr_app.data.remote.request

import com.google.gson.annotations.SerializedName

data class UpdateObservationRequest(

    val id: Long,

    val message: String,

    @SerializedName("image_url")
    val imageUrl: String?
)