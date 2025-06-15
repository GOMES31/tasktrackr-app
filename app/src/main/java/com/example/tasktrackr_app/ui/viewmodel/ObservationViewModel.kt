package com.example.tasktrackr_app.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tasktrackr_app.data.remote.api.ApiClient
import com.example.tasktrackr_app.data.remote.api.ObservationApi
import com.example.tasktrackr_app.data.remote.request.CreateObservationRequest
import com.example.tasktrackr_app.data.remote.request.UpdateObservationRequest
import com.example.tasktrackr_app.data.remote.response.ApiResponse
import com.example.tasktrackr_app.data.remote.response.data.ObservationData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Response

class ObservationViewModel(application: Application) : AndroidViewModel(application) {
    private val observationApi: ObservationApi = ApiClient.observationApi(application)

    private val _currentObservation = MutableStateFlow<ObservationData?>(null)
    val currentObservation = _currentObservation.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    fun createObservation(taskId: Long, message: String, imageUrl: String?) {
        _loading.value = true
        _error.value = null
        viewModelScope.launch {
            try {
                val request = CreateObservationRequest(taskId, message, imageUrl)
                val response: Response<ApiResponse<ObservationData>> = observationApi.createObservation(request)
                if (response.isSuccessful) {
                    response.body()?.data?.let {
                        _currentObservation.value = it
                    }
                } else {
                    _error.value = response.errorBody()?.string() ?: "Unknown error"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Network error"
            } finally {
                _loading.value = false
            }
        }
    }

    fun getObservationById(id: Long) {
        _loading.value = true
        _error.value = null
        viewModelScope.launch {
            try {
                val response: Response<ApiResponse<ObservationData>> = observationApi.getObservationById(id)
                if (response.isSuccessful) {
                    _currentObservation.value = response.body()?.data
                } else {
                    _error.value = response.errorBody()?.string() ?: "Observation not found"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Network error"
            } finally {
                _loading.value = false
            }
        }
    }

    fun updateObservation(id: Long, message: String, imageUrl: String?) {
        _loading.value = true
        _error.value = null
        viewModelScope.launch {
            try {
                val request = UpdateObservationRequest(id, message, imageUrl)
                val response: Response<ApiResponse<ObservationData>> = observationApi.updateObservation(request)
                if (response.isSuccessful) {
                    _currentObservation.value = response.body()?.data
                } else {
                    _error.value = response.errorBody()?.string() ?: "Failed to update observation"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Network error"
            } finally {
                _loading.value = false
            }
        }
    }

    fun clearData() {
        _currentObservation.value = null
        _loading.value = false
        _error.value = null
    }

}