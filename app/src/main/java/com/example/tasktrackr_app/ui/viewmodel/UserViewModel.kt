package com.example.tasktrackr_app.ui.viewmodel

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tasktrackr_app.data.local.entity.UserEntity
import com.example.tasktrackr_app.data.local.repository.UserRepository
import com.example.tasktrackr_app.data.remote.response.data.TeamData
import com.example.tasktrackr_app.data.remote.api.ApiClient
import com.example.tasktrackr_app.data.remote.api.UserApi
import com.example.tasktrackr_app.data.remote.request.UpdateUserProfileRequest
import com.example.tasktrackr_app.data.remote.response.data.AuthData
import com.example.tasktrackr_app.data.remote.response.data.UserProfileData
import com.example.tasktrackr_app.utils.LocalImageStorage
import com.example.tasktrackr_app.data.remote.response.data.TaskData
import com.example.tasktrackr_app.data.remote.response.data.ProjectData
import com.example.tasktrackr_app.data.remote.response.data.ObservationData
import com.example.tasktrackr_app.utils.EncryptedPrefsUtil
import com.example.tasktrackr_app.utils.NetworkChangeReceiver
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserViewModel(application: Application) : AndroidViewModel(application) {
    private val userApi: UserApi = ApiClient.userApi(application)
    private val userRepository: UserRepository = UserRepository(application)

    private val _userData = MutableStateFlow<AuthData?>(null)
    val userData = _userData.asStateFlow()

    private val _profileData = MutableStateFlow<UserProfileData?>(null)
    val profileData = _profileData.asStateFlow()

    private val _errorCode = MutableStateFlow<Int?>(null)

    private val _userTeams = MutableStateFlow<List<TeamData>?>(null)
    val userTeams = _userTeams.asStateFlow()

    private val _isLoadingTeams = MutableStateFlow(false)
    val isLoadingTeams = _isLoadingTeams.asStateFlow()

    private val _updateProfileSuccess = MutableStateFlow(false)
    val updateProfileSuccess = _updateProfileSuccess.asStateFlow()

    private val _userTasks = MutableStateFlow<List<TaskData>?>(null)
    val userTasks = _userTasks.asStateFlow()

    private val _isLoadingTasks = MutableStateFlow(false)
    val isLoadingTasks = _isLoadingTasks.asStateFlow()

    private val _userProjects = MutableStateFlow<List<ProjectData>?>(null)
    val userProjects = _userProjects.asStateFlow()

    private val _isLoadingProjects = MutableStateFlow(false)
    val isLoadingProjects = _isLoadingProjects.asStateFlow()

    private val _userObservations = MutableStateFlow<List<ObservationData>?>(null)
    val userObservations = _userObservations.asStateFlow()

    private val _isLoadingObservations = MutableStateFlow(false)
    val isLoadingObservations = _isLoadingObservations.asStateFlow()

    fun loadProfile(data: AuthData) {
        _userData.value = data
        _profileData.value = UserProfileData(
            name = data.name,
            email = data.email,
            avatarUrl = data.avatarUrl
        )

        viewModelScope.launch {
            val user = UserEntity(
                email = data.email,
                name = data.name,
                avatarUrl = data.avatarUrl,
                isSynced = NetworkChangeReceiver.isWifiConnected(getApplication())
            )
            userRepository.insertUser(user)
        }
    }

    fun updateProfile(name: String?, password: String?, avatarUri: Uri?) {
        _errorCode.value = null

        viewModelScope.launch {
            val localAvatarPath = if (avatarUri != null) {
                Log.d("UserViewModel", "Processing new avatar: $avatarUri")
                LocalImageStorage.saveProfileImage(getApplication(), avatarUri)
            } else {
                profileData.value?.avatarUrl
            }

            val updatedName = name ?: profileData.value?.name
            val updatedEmail = profileData.value?.email
            val currentTime = System.currentTimeMillis()

            val userEntity = UserEntity(
                email = updatedEmail ?: return@launch,
                name = updatedName ?: return@launch,
                avatarUrl = localAvatarPath,
                updatedAt = currentTime,
                isSynced = NetworkChangeReceiver.isWifiConnected(getApplication())
            )

            userRepository.insertUser(userEntity)

            _profileData.value = _profileData.value?.copy(
                name = updatedName,
                avatarUrl = localAvatarPath
            )

            // Save the updated password locally if not connected to Wi-Fi
            if (!NetworkChangeReceiver.isWifiConnected(getApplication()) && !password.isNullOrEmpty()) {
                EncryptedPrefsUtil.savePassword(getApplication(), password)
            }

            if (NetworkChangeReceiver.isWifiConnected(getApplication())) {
                Log.d("UserViewModel", "Wi-Fi is connected. Attempting to update profile via API.")
                try {
                    val request = UpdateUserProfileRequest(
                        name = updatedName,
                        password = password,
                        avatarUrl = localAvatarPath,
                        updatedAt = currentTime
                    )
                    Log.d("UserViewModel", "Sending updateProfile request: $request")
                    val response = userApi.updateProfile(request)
                    Log.d("UserViewModel", "API response: code=${response.code()}, success=${response.isSuccessful}")
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
            } else {
                _updateProfileSuccess.value = true
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
                } else {
                    Log.e("UserViewModel", "Error fetching teams: ${response.code()}")
                    _userTeams.value = emptyList()
                }
            } catch (e: Exception) {
                Log.e("UserViewModel", "Error fetching teams", e)
                _userTeams.value = emptyList()
            } finally {
                _isLoadingTeams.value = false
            }
        }
    }

    fun fetchUserTasks() {
        viewModelScope.launch {
            _isLoadingTasks.value = true
            try {
                val response = userApi.getUserTasks()
                if (response.isSuccessful) {
                    response.body()?.data?.let { tasks ->
                        _userTasks.value = tasks
                    }
                } else {
                    Log.e("UserViewModel", "Error fetching tasks: ${response.code()}")
                    _userTasks.value = emptyList()
                }
            } catch (e: Exception) {
                Log.e("UserViewModel", "Error fetching tasks", e)
                _userTasks.value = emptyList()
            } finally {
                _isLoadingTasks.value = false
            }
        }
    }

    fun fetchUserProjects() {
        viewModelScope.launch {
            _isLoadingProjects.value = true
            try {
                val response = userApi.getUserProjects()
                if (response.isSuccessful) {
                    response.body()?.data?.let { projects ->
                        _userProjects.value = projects
                    }
                } else {
                    Log.e("UserViewModel", "Error fetching projects: ${response.code()}")
                    _userProjects.value = emptyList()
                }
            } catch (e: Exception) {
                Log.e("UserViewModel", "Error fetching projects", e)
                _userProjects.value = emptyList()
            } finally {
                _isLoadingProjects.value = false
            }
        }
    }

    fun fetchUserObservations() {
        viewModelScope.launch {
            _isLoadingObservations.value = true
            try {
                val response = userApi.getUserObservations()
                if (response.isSuccessful) {
                    response.body()?.data?.let { observations ->
                        _userObservations.value = observations
                    }
                } else {
                    Log.e("UserViewModel", "Error fetching observations: ${response.code()}")
                    _userObservations.value = emptyList()
                }
            } catch (e: Exception) {
                Log.e("UserViewModel", "Error fetching observations", e)
                _userObservations.value = emptyList()
            } finally {
                _isLoadingObservations.value = false
            }
        }
    }

    fun clearData() {

        _userData.value = null
        _profileData.value = null
        _errorCode.value = null
        _isLoadingTeams.value = false
        _updateProfileSuccess.value = false
        _userTasks.value = null
        _isLoadingTasks.value = false
        _userProjects.value = null
        _isLoadingProjects.value = false
        _userObservations.value = null
        _isLoadingObservations.value = false
    }
}




