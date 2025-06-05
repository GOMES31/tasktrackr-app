package com.example.tasktrackr_app.data.remote.api

import com.example.tasktrackr_app.data.remote.request.CreateObservationRequest
import com.example.tasktrackr_app.data.remote.request.UpdateObservationRequest
import com.example.tasktrackr_app.data.remote.response.ApiResponse
import com.example.tasktrackr_app.data.remote.response.data.ObservationData
import retrofit2.Response
import retrofit2.http.*

interface ObservationApi {

    @POST("observations/create")
    suspend fun createObservation(@Body body: CreateObservationRequest):
            Response<ApiResponse<ObservationData>>

    @GET("observations/{id}")
    suspend fun getObservationById(@Path("id") id: Long):
            Response<ApiResponse<ObservationData>>

    @PUT("observations/update")
    suspend fun updateObservation(@Body body: UpdateObservationRequest):
            Response<ApiResponse<ObservationData>>
}