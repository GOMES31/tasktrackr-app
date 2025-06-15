package com.example.tasktrackr_app.data.remote.api

import com.example.tasktrackr_app.data.remote.request.CreateProjectRequest
import com.example.tasktrackr_app.data.remote.request.UpdateProjectRequest
import com.example.tasktrackr_app.data.remote.response.ApiResponse
import com.example.tasktrackr_app.data.remote.response.data.ProjectData
import com.example.tasktrackr_app.data.remote.response.data.TaskData
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ProjectApi {

    @POST("projects/create")
    suspend fun createProject(@Body body: CreateProjectRequest):
            Response<ApiResponse<ProjectData>>

    @GET("projects/{id}")
    suspend fun getProjectById(@Path("id") id: Long):
            Response<ApiResponse<ProjectData>>

    @PUT("projects/update")
    suspend fun updateProject(@Body body: UpdateProjectRequest):
            Response<ApiResponse<ProjectData>>

    @GET("projects/{projectId}/tasks")
    suspend fun getTasksForProject(@Path("projectId") projectId: Long):
            Response<ApiResponse<List<TaskData>>>
}