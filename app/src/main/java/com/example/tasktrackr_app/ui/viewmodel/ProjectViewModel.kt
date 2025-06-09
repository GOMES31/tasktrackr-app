package com.example.tasktrackr_app.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tasktrackr_app.data.remote.api.ApiClient
import com.example.tasktrackr_app.data.remote.api.ProjectApi
import com.example.tasktrackr_app.data.remote.request.CreateProjectRequest
import com.example.tasktrackr_app.data.remote.request.UpdateProjectRequest
import com.example.tasktrackr_app.data.remote.response.ApiResponse
import com.example.tasktrackr_app.data.remote.response.data.ProjectData
import com.example.tasktrackr_app.data.remote.response.data.TaskData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Response
import java.util.Date

class ProjectViewModel(application: Application) : AndroidViewModel(application) {

    private val projectApi: ProjectApi = ApiClient.projectApi(application)

    private val _currentProject = MutableStateFlow<ProjectData?>(null)
    val currentProject = _currentProject.asStateFlow()

    private val _tasksForProject = MutableStateFlow<List<TaskData>>(emptyList())
    val tasksForProject = _tasksForProject.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    fun createProject(name: String, description: String, teamId: Long, startDate: Date?, endDate: Date?, status: String) {
        _loading.value = true
        _error.value = null

        viewModelScope.launch {
            try {
                val request = CreateProjectRequest(name, description, teamId, startDate, endDate, status)
                val response: Response<ApiResponse<ProjectData>> = projectApi.createProject(request)

                if (response.isSuccessful) {
                    response.body()?.data?.let {
                        _currentProject.value = it
                    }
                } else {
                    _error.value = response.errorBody()?.string() ?: "Unknown error"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Network error"
                Log.e("ProjectViewModel", "createProject error", e)
            } finally {
                _loading.value = false
            }
        }
    }

    fun getProjectById(id: Long) {
        _loading.value = true
        _error.value = null

        viewModelScope.launch {
            try {
                val response: Response<ApiResponse<ProjectData>> = projectApi.getProjectById(id)

                if (response.isSuccessful) {
                    _currentProject.value = response.body()?.data
                } else {
                    _error.value = response.errorBody()?.string() ?: "Project not found"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Network error"
                Log.e("ProjectViewModel", "getProjectById error", e)
            } finally {
                _loading.value = false
            }
        }
    }

    fun updateProject(id: Long, name: String?, description: String?, startDate: Date?, endDate: Date?, status: String?) {
        _loading.value = true
        _error.value = null

        viewModelScope.launch {
            try {
                val request = UpdateProjectRequest(id, name, description, startDate, endDate, status)
                val response: Response<ApiResponse<ProjectData>> = projectApi.updateProject(request)

                if (response.isSuccessful) {
                    _currentProject.value = response.body()?.data
                } else {
                    _error.value = response.errorBody()?.string() ?: "Failed to update project"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Network error"
                Log.e("ProjectViewModel", "updateProject error", e)
            } finally {
                _loading.value = false
            }
        }
    }

    fun getTasksForProject(projectId: Long) {
        _loading.value = true
        _error.value = null

        viewModelScope.launch {
            try {
                val response: Response<ApiResponse<List<TaskData>>> = projectApi.getTasksForProject(projectId)

                if (response.isSuccessful) {
                    _tasksForProject.value = response.body()?.data ?: emptyList()
                } else {
                    _error.value = response.errorBody()?.string() ?: "Failed to retrieve tasks for project"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Network error"
                Log.e("ProjectViewModel", "getTasksForProject error", e)
            } finally {
                _loading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}