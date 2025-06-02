package com.example.tasktrackr_app.ui.viewmodel

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tasktrackr_app.data.remote.api.ApiClient
import com.example.tasktrackr_app.data.remote.api.TeamApi
import com.example.tasktrackr_app.data.remote.request.CreateTeamRequest
import com.example.tasktrackr_app.utils.LocalImageStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TeamViewModel(application: Application) : AndroidViewModel(application) {
    private val teamApi: TeamApi = ApiClient.teamApi(application)
    private val _errorCode = MutableStateFlow<Int?>(null)
    val errorCode = _errorCode.asStateFlow()

    fun createTeam(name: String, department: String, logoUri: Uri? = null) {
        viewModelScope.launch {
            try {
                val logoPath = logoUri?.let { uri ->
                    Log.d("TeamViewModel", "Processing team logo: $uri")
                    LocalImageStorage.saveTeamLogo(getApplication(), uri)
                }

                val request = CreateTeamRequest(
                    name = name,
                    department = department,
                    imageUrl = logoPath
                )

                try {
                    val response = teamApi.createTeam(request)
                    Log.d("TeamViewModel", "Response code: ${response.code()}")

                    if (!response.isSuccessful) {
                        Log.e("TeamViewModel", "Error response: ${response.errorBody()?.string()}")
                        _errorCode.value = response.code()
                    }
                } catch (e: Exception) {
                    Log.e("TeamViewModel", "Network error", e)
                    _errorCode.value = -1
                }
            } catch (e: Exception) {
                Log.e("TeamViewModel", "Error creating team", e)
                _errorCode.value = -1
            }
        }
    }
}

