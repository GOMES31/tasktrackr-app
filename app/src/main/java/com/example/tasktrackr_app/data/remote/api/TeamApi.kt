package com.example.tasktrackr_app.data.remote.api

import com.example.tasktrackr_app.data.remote.request.CreateTeamRequest
import com.example.tasktrackr_app.data.remote.response.ApiResponse
import com.example.tasktrackr_app.data.remote.response.data.TeamData
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface TeamApi {

    @POST("team/create")
    suspend fun createTeam(@Body body: CreateTeamRequest):
            Response<ApiResponse<Void>>

    @GET("team/{teamId}")
    suspend fun getTeam(@Path("teamId") teamId: String):
            Response<ApiResponse<TeamData>>

}
