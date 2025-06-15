package com.example.tasktrackr_app.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tasktrackr_app.data.remote.api.ApiClient
import com.example.tasktrackr_app.data.remote.api.TaskApi
import com.example.tasktrackr_app.data.remote.request.CreateTaskRequest
import com.example.tasktrackr_app.data.remote.request.UpdateTaskRequest
import com.example.tasktrackr_app.data.remote.response.data.ObservationData
import com.example.tasktrackr_app.data.remote.response.data.TaskData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Response
import java.util.Date

class TaskViewModel(application: Application) : AndroidViewModel(application) {
    private val taskApi: TaskApi = ApiClient.taskApi(application)

    private val _currentTask = MutableStateFlow<TaskData?>(null)
    val currentTask = _currentTask.asStateFlow()

    private val _observations = MutableStateFlow<List<ObservationData>>(emptyList())
    val observations = _observations.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    private val _operationSuccess = MutableStateFlow(false)
    val operationSuccess = _operationSuccess.asStateFlow()

    fun createTask(
        title: String,
        description: String,
        projectId: Long,
        startDate: Date? = null,
        endDate: Date? = null,
        status: String? = null,
        assigneeIds: List<Long>? = null
    ) {
        _isLoading.value = true
        _errorMessage.value = null
        _operationSuccess.value = false
        viewModelScope.launch {
            try {
                val request = CreateTaskRequest(title, description, projectId, startDate, endDate, status, assigneeIds)
                val response: Response<com.example.tasktrackr_app.data.remote.response.ApiResponse<TaskData>> = taskApi.createTask(request)
                if (response.isSuccessful) {
                    response.body()?.data?.let {
                        _currentTask.value = it
                        _currentTask.value = null
                        _operationSuccess.value = true
                    }
                } else {
                    _errorMessage.value = response.errorBody()?.string() ?: "Unknown error"
                }
            } catch (t: Throwable) {
                _errorMessage.value = t.message ?: "Network error"
                Log.e("TaskViewModel", "createTask error", t)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getTaskById(id: Long) {
        _isLoading.value = true
        _errorMessage.value = null
        _operationSuccess.value = false
        viewModelScope.launch {
            try {
                val response: Response<com.example.tasktrackr_app.data.remote.response.ApiResponse<TaskData>> = taskApi.getTaskById(id)
                if (response.isSuccessful) {
                    _currentTask.value = response.body()?.data
                    Log.d("TaskViewModel", "Task fetched successfully: ${_currentTask.value?.title}")
                } else {
                    _errorMessage.value = response.errorBody()?.string() ?: "Unknown error"
                    Log.e("TaskViewModel", "Get task failed: ${_errorMessage.value}")
                }
            } catch (t: Throwable) {
                _errorMessage.value = t.message ?: "Network error"
                Log.e("TaskViewModel", "getTaskById error", t)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateTask(
        id: Long,
        title: String? = null,
        description: String? = null,
        status: String? = null,
        startDate: Date? = null,
        endDate: Date? = null,
        assigneeIds: List<Long>? = null
    ) {
        _isLoading.value = true
        _errorMessage.value = null
        _operationSuccess.value = false
        viewModelScope.launch {
            try {
                val request = UpdateTaskRequest(id, title, description, status, startDate, endDate, assigneeIds)
                val response: Response<com.example.tasktrackr_app.data.remote.response.ApiResponse<TaskData>> = taskApi.updateTask(request)
                if (response.isSuccessful) {
                    response.body()?.data?.let {
                        _currentTask.value = it
                        _currentTask.value = null
                        _operationSuccess.value = true
                    }
                } else {
                    _errorMessage.value = response.errorBody()?.string() ?: "Unknown error"
                    Log.e("TaskViewModel", "Update task failed: ${_errorMessage.value}")
                }
            } catch (t: Throwable) {
                _errorMessage.value = t.message ?: "Network error"
                Log.e("TaskViewModel", "updateTask error", t)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getObservationsForTask(taskId: Long) {
        _isLoading.value = true
        _errorMessage.value = null
        viewModelScope.launch {
            try {
                val response: Response<com.example.tasktrackr_app.data.remote.response.ApiResponse<List<ObservationData>>> = taskApi.getObservationsForTask(taskId)
                if (response.isSuccessful) {
                    _observations.value = response.body()?.data ?: emptyList()
                } else {
                    _errorMessage.value = response.errorBody()?.string() ?: "Unknown error"
                }
            } catch (t: Throwable) {
                _errorMessage.value = t.message ?: "Network error"
                Log.e("TaskViewModel", "getObservationsForTask error", t)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearData() {
        _currentTask.value = null
        _observations.value = emptyList()
        _isLoading.value = false
        _errorMessage.value = null
        _operationSuccess.value = false
    }

}
