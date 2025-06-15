package com.example.tasktrackr_app.data.remote.response

data class ApiResponse<T>(
    val status: Int,
    val message: String,
    val data: T
)