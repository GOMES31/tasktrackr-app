package com.example.tasktrackr_app.ui.viewmodel

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tasktrackr_app.data.local.TokenRepository
import com.example.tasktrackr_app.data.remote.response.data.TeamData
import com.example.tasktrackr_app.data.remote.api.ApiClient
import com.example.tasktrackr_app.data.remote.api.UserApi
import com.example.tasktrackr_app.data.remote.request.UpdateUserProfileRequest
import com.example.tasktrackr_app.data.remote.response.ApiResponse
import com.example.tasktrackr_app.data.remote.response.data.AuthData
import com.example.tasktrackr_app.data.remote.response.data.UserProfileData
import com.example.tasktrackr_app.utils.LocalImageStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Response

class UserViewModel(application: Application) : AndroidViewModel(application) {
    private val userApi: UserApi = ApiClient.userApi(application)
    private val tokenRepository: TokenRepository = TokenRepository(application)
    private val _userData = MutableStateFlow<AuthData?>(null)
    val userData = _userData.asStateFlow()
    private val _profileData = MutableStateFlow<UserProfileData?>(null)
    private val profileData = _profileData.asStateFlow()
    private val _errorCode = MutableStateFlow<Int?>(null)
    private val _userTeams = MutableStateFlow<List<TeamData>?>(null)
    val userTeams = _userTeams.asStateFlow()
    private val _isLoadingTeams = MutableStateFlow(false)
    val isLoadingTeams = _isLoadingTeams.asStateFlow()
    private val _updateProfileSuccess = MutableStateFlow(false)
    val updateProfileSuccess = _updateProfileSuccess.asStateFlow()

    fun loadProfile(data: AuthData) {
        _userData.value = data
        // Initialize profileData from AuthData
        _profileData.value = UserProfileData(
            name = data.name,
            email = data.email,
            avatarUrl = data.avatarUrl
        )
    }

    fun updateProfile(name: String?, password: String?, avatarUri: Uri?) {
        _errorCode.value = null
        viewModelScope.launch {
            try {
                val localAvatarPath = if (avatarUri != null) {
                    Log.d("UserViewModel", "Processing new avatar: $avatarUri")
                    LocalImageStorage.saveProfileImage(getApplication(), avatarUri)
                } else {
                    profileData.value?.avatarUrl
                }

                val request = UpdateUserProfileRequest(
                    name = name ?: profileData.value?.name,
                    password = password,
                    avatarUrl = localAvatarPath
                )

                val response = userApi.updateProfile(request)

                if (response.isSuccessful) {
                    val updatedProfile = response.body()?.data
                    if (updatedProfile != null) {
                        _profileData.value = updatedProfile
                        _userData.value = updatedProfile.avatarUrl?.let {
                            _userData.value?.copy(
                                name = updatedProfile.name,
                                avatarUrl = it
                            )
                        }
                        _updateProfileSuccess.value = true
                    }
                } else {
                    _errorCode.value = response.code()
                }
            } catch (e: Exception) {
                Log.e("UserViewModel", "Error updating profile", e)
                _errorCode.value = -1
            }
        }
    }

    fun resetUpdateProfileSuccess() {
        _updateProfileSuccess.value = false
    }

    fun fetchUserTeams() {
        viewModelScope.launch {
            _isLoadingTeams.value = true
            try {
                val response = userApi.getUserTeams()
                if (response.isSuccessful) {
                    response.body()?.data?.let { teams ->
                        _userTeams.value = teams
                    }
                }
            } catch (e: Exception) {
                Log.e("UserViewModel", "Error fetching teams", e)
                _userTeams.value = emptyList()
            } finally {
                _isLoadingTeams.value = false
            }
        }
    }

    fun clearData() {
        _userData.value = null
        _profileData.value = null
        _userTeams.value = null
        _errorCode.value = null
        _isLoadingTeams.value = false
        _updateProfileSuccess.value = false
    }
}
