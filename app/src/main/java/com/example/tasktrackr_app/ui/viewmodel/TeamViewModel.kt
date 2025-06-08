package com.example.tasktrackr_app.ui.viewmodel

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tasktrackr_app.data.remote.api.ApiClient
import com.example.tasktrackr_app.data.remote.api.TeamApi
import com.example.tasktrackr_app.data.local.TokenRepository
import com.example.tasktrackr_app.data.remote.request.AddTeamMemberRequest
import com.example.tasktrackr_app.data.remote.request.CreateTeamRequest
import com.example.tasktrackr_app.data.remote.request.UpdateTeamMemberRequest
import com.example.tasktrackr_app.data.remote.request.UpdateTeamRequest
import com.example.tasktrackr_app.data.remote.response.data.TeamData
import com.example.tasktrackr_app.utils.LocalImageStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TeamViewModel(application: Application) : AndroidViewModel(application) {
    private val teamApi: TeamApi = ApiClient.teamApi(application)
    private val tokenRepository: TokenRepository = TokenRepository(application)
    private val _errorCode = MutableStateFlow<Int?>(null)
    val errorCode = _errorCode.asStateFlow()
    private val _selectedTeam = MutableStateFlow<TeamData?>(null)
    val selectedTeam = _selectedTeam.asStateFlow()
    private val _memberRemoved = MutableStateFlow(false)
    val memberRemoved = _memberRemoved.asStateFlow()
    private val _updateTeamSuccess = MutableStateFlow(false)
    val updateTeamSuccess = _updateTeamSuccess.asStateFlow()
    private val _createTeamSuccess = MutableStateFlow(false)
    val createTeamSuccess = _createTeamSuccess.asStateFlow()
    private val _addMemberSuccess = MutableStateFlow(false)
    val addMemberSuccess = _addMemberSuccess.asStateFlow()
    private val _updateMemberSuccess = MutableStateFlow(false)
    val updateMemberSuccess = _updateMemberSuccess.asStateFlow()

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

                    if (response.isSuccessful) {
                        _createTeamSuccess.value = true
                    } else {
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

    fun loadTeam(teamId: String) {
        viewModelScope.launch {
            try {
                val response = teamApi.getTeam(teamId)
                if (response.isSuccessful) {
                    response.body()?.data?.let { team ->
                        _selectedTeam.value = team
                    }
                } else {
                    _errorCode.value = response.code()
                }
            } catch (e: Exception) {
                Log.e("TeamViewModel", "Error loading team", e)
                _errorCode.value = -1
            }
        }
    }

    fun updateTeam(teamId: String, name: String?, department: String?, logoUri: Uri?) {
        viewModelScope.launch {
            try {
                val logoPath = logoUri?.let { uri ->
                    Log.d("TeamViewModel", "Processing team logo: $uri")
                    LocalImageStorage.saveTeamLogo(getApplication(), uri)
                }

                val request = UpdateTeamRequest(
                    name = name,
                    department = department,
                    imageUrl = logoPath
                )


                try {
                    val response = teamApi.updateTeam(teamId, request)
                    Log.d("TeamViewModel", "Response code: ${response.code()}")

                    if (response.isSuccessful) {
                        response.body()?.data?.let { team ->
                            _selectedTeam.value = team
                            _updateTeamSuccess.value = true
                        }
                    } else {
                        Log.e("TeamViewModel", "Error response: ${response.errorBody()?.string()}")
                        _errorCode.value = response.code()
                    }
                } catch (e: Exception) {
                    Log.e("TeamViewModel", "Network error", e)
                    _errorCode.value = -1
                }
            } catch (e: Exception) {
                Log.e("TeamViewModel", "Error updating team", e)
                _errorCode.value = -1
            }
        }
    }

    fun removeMember(teamId: String, memberId: Long) {
        viewModelScope.launch {

            try {
                val response = teamApi.removeMember(teamId, memberId)
                Log.d("TeamViewModel", "Response code: ${response.code()}")

                if (response.isSuccessful) {
                    loadTeam(teamId)
                    _memberRemoved.value = true
                } else {
                    Log.e("TeamViewModel", "Error response: ${response.errorBody()?.string()}")
                    _errorCode.value = response.code()
                }
            } catch (e: Exception) {
                Log.e("TeamViewModel", "Error removing member", e)
                _errorCode.value = -1
            }
        }
    }

    fun addMember(teamId: String, email: String, role: String) {

        val request = AddTeamMemberRequest(
            email = email,
            role = role
        )

        viewModelScope.launch {
            try {
                val response = teamApi.addMember(teamId,request)
                Log.d("TeamViewModel", "Response code: ${response.code()}")

                if (response.isSuccessful) {
                    loadTeam(teamId)
                    _addMemberSuccess.value = true
                } else {
                    Log.e("TeamViewModel", "Error response: ${response.errorBody()?.string()}")
                    _errorCode.value = response.code()
                }
            } catch (e: Exception) {
                Log.e("TeamViewModel", "Error adding member", e)
                _errorCode.value = -1
            }
        }
    }

    fun updateMember(teamId: String, memberId: Long, role: String) {
        val request = UpdateTeamMemberRequest(role = role)

        viewModelScope.launch {
            try {
                val response = teamApi.updateMember(teamId, memberId, request)
                Log.d("TeamViewModel", "Response code: ${response.code()}")

                if (response.isSuccessful) {
                    loadTeam(teamId)
                    _updateMemberSuccess.value = true
                } else {
                    Log.e("TeamViewModel", "Error response: ${response.errorBody()?.string()}")
                    _errorCode.value = response.code()
                }
            } catch (e: Exception) {
                Log.e("TeamViewModel", "Error updating member", e)
                _errorCode.value = -1
            }
        }
    }

    fun resetMemberRemoved() {
        _memberRemoved.value = false
    }

    fun resetUpdateTeamSuccess() {
        _updateTeamSuccess.value = false
    }

    fun resetCreateTeamSuccess() {
        _createTeamSuccess.value = false
    }

    fun resetAddMemberSuccess() {
        _addMemberSuccess.value = false
    }

    fun resetUpdateMemberSuccess() {
        _updateMemberSuccess.value = false
    }

    fun clearData() {
        _errorCode.value = null
        _selectedTeam.value = null
        _memberRemoved.value = false
        _updateTeamSuccess.value = false
        _createTeamSuccess.value = false
        _addMemberSuccess.value = false
    }

}
