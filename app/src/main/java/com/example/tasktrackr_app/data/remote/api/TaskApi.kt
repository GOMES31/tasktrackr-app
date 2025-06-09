package com.example.tasktrackr_app.data.remote.api

import com.example.tasktrackr_app.data.remote.request.CreateTaskRequest
import com.example.tasktrackr_app.data.remote.request.UpdateTaskRequest
import com.example.tasktrackr_app.data.remote.response.ApiResponse
import com.example.tasktrackr_app.data.remote.response.data.TaskData
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface TaskApi {

    @POST("tasks/create")
    suspend fun createTask(@Body body: CreateTaskRequest):
            Response<ApiResponse<TaskData>>

    @GET("tasks/{id}")
    suspend fun getTaskById(@Path("id") id: Long):
            Response<ApiResponse<TaskData>>

    @PUT("tasks/update")
    suspend fun updateTask(@Body body: UpdateTaskRequest):
            Response<ApiResponse<TaskData>>

    @GET("tasks/{id}/observations")
    suspend fun getObservationsForTask(@Path("id") id: Long):
            Response<ApiResponse<List<TaskData.ObservationData>>>
}