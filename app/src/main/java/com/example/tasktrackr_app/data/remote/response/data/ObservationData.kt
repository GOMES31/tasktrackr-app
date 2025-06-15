package com.example.tasktrackr_app.data.remote.response.data

import com.google.gson.annotations.SerializedName
import java.util.Date

data class ObservationData(
    @SerializedName("id")
    val id: Long,

    @SerializedName("message")
    val message: String,

    @SerializedName("created_at")
    val createdAt: Date?,

    @SerializedName("task")
    val task: TaskInfo,

    @SerializedName("user")
    val user: UserInfo,

    @SerializedName("image")
    val image: ImageInfo?
) {
    data class TaskInfo(
        @SerializedName("id")
        val id: Long,

        @SerializedName("title")
        val title: String
    )

    data class UserInfo(
        @SerializedName("id")
        val id: Long,

        @SerializedName("name")
        val name: String,

        @SerializedName("email")
        val email: String
    )

    data class ImageInfo(
        @SerializedName("id")
        val id: Long,

        @SerializedName("image_url")
        val imageUrl: String,

        @SerializedName("uploaded_at")
        val uploadedAt: Date?
    )
}