package com.example.tasktrackr_app.data.remote.api

import com.example.tasktrackr_app.data.remote.response.data.TeamData
import com.example.tasktrackr_app.data.remote.request.UpdateUserProfileRequest
import com.example.tasktrackr_app.data.remote.response.ApiResponse
import com.example.tasktrackr_app.data.remote.response.data.UserProfileData
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.PUT

interface UserApi {

    @PUT("user/update-profile")
    suspend fun updateProfile(@Body body: UpdateUserProfileRequest):
            Response<ApiResponse<UserProfileData>>

    @GET("user/teams")
    suspend fun getUserTeams():
            Response<ApiResponse<List<TeamData>>>
}
