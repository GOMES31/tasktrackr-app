package com.example.tasktrackr_app.data.remote.api

import com.example.tasktrackr_app.data.remote.request.CreateTeamRequest
import com.example.tasktrackr_app.data.remote.request.UpdateTeamRequest
import com.example.tasktrackr_app.data.remote.request.AddTeamMemberRequest
import com.example.tasktrackr_app.data.remote.request.UpdateTeamMemberRequest
import com.example.tasktrackr_app.data.remote.response.ApiResponse
import com.example.tasktrackr_app.data.remote.response.data.TeamData
import com.example.tasktrackr_app.data.remote.response.data.TeamMemberData
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PATCH
import retrofit2.http.DELETE
import retrofit2.http.PUT
import retrofit2.http.Path

interface TeamApi {

    @POST("team/create")
    suspend fun createTeam(@Body body: CreateTeamRequest):
            Response<ApiResponse<TeamData>>

    @GET("team/{teamId}")
    suspend fun getTeam(@Path("teamId") teamId: String):
            Response<ApiResponse<TeamData>>

    @PUT("team/{teamId}/update-profile")
    suspend fun updateTeam(@Path("teamId") teamId: String, @Body body: UpdateTeamRequest):
            Response<ApiResponse<TeamData>>

    @DELETE("team/{teamId}/remove-member/{memberId}")
    suspend fun removeMember(@Path("teamId") teamId: String, @Path("memberId") memberId: Long):
            Response<ApiResponse<Void>>

    @POST("team/{teamId}/add-member")
    suspend fun addMember(@Path("teamId") teamId: String, @Body request: AddTeamMemberRequest):
            Response<ApiResponse<TeamMemberData>>

    @PUT("team/{teamId}/update-member/{memberId}")
    suspend fun updateMember(@Path("teamId") teamId: String, @Path("memberId") memberId: Long,
                             @Body request: UpdateTeamMemberRequest):
            Response<ApiResponse<TeamMemberData>>
}
