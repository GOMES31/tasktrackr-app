package com.example.tasktrackr_app.data.remote.request

import com.google.gson.annotations.SerializedName

data class CreateTeamRequest(
    val name: String,

    val department: String,

    @SerializedName("image_url")
    val imageUrl: String?
)
