package com.example.tasktrackr_app.ui.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tasktrackr_app.data.local.entity.TeamEntity
import com.example.tasktrackr_app.data.local.entity.TeamMemberEntity
import com.example.tasktrackr_app.data.local.entity.UserEntity
import com.example.tasktrackr_app.data.local.repository.TeamMemberRepository
import com.example.tasktrackr_app.data.local.repository.TeamRepository
import com.example.tasktrackr_app.data.local.repository.UserRepository
import com.example.tasktrackr_app.data.remote.api.ApiClient
import com.example.tasktrackr_app.data.remote.api.UserApi
import com.example.tasktrackr_app.data.remote.request.UpdateUserProfileRequest
import com.example.tasktrackr_app.data.remote.response.data.AuthData
import com.example.tasktrackr_app.data.remote.response.data.ObservationData
import com.example.tasktrackr_app.data.remote.response.data.ProjectData
import com.example.tasktrackr_app.data.remote.response.data.TaskData
import com.example.tasktrackr_app.data.remote.response.data.TeamData
import com.example.tasktrackr_app.data.remote.response.data.TeamMemberData
import com.example.tasktrackr_app.data.remote.response.data.UserProfileData
import com.example.tasktrackr_app.utils.EncryptedPrefsUtil
import com.example.tasktrackr_app.utils.LocalImageStorage
import com.example.tasktrackr_app.utils.NetworkChangeReceiver
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class UserViewModel(application: Application) : AndroidViewModel(application) {
    private val userApi: UserApi = ApiClient.userApi(application)
    private val userRepository: UserRepository = UserRepository(application)

    private val _userData = MutableStateFlow<AuthData?>(null)

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
                try {
                    val request = UpdateUserProfileRequest(
                        name = updatedName,
                        password = password,
                        avatarUrl = localAvatarPath,
                        updatedAt = currentTime
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

            if (NetworkChangeReceiver.isWifiConnected(getApplication())) {
                try {
                    val response = userApi.getUserTeams()
                    if (response.isSuccessful) {
                        response.body()?.data?.let { teamsFromApi ->
                            _userTeams.value = teamsFromApi

                            val teamRepository = TeamRepository(getApplication())
                            val teamMemberRepository = TeamMemberRepository(getApplication())

                            for (teamDataApi in teamsFromApi) {
                                val teamEntity = TeamEntity(
                                    id = teamDataApi.id.toString(),
                                    name = teamDataApi.name,
                                    department = teamDataApi.department,
                                    imageUrl = teamDataApi.imageUrl,
                                    isSynced = true,
                                    updatedAt = System.currentTimeMillis()
                                )
                                teamRepository.insertTeam(teamEntity)

                                teamMemberRepository.deleteTeamMembers(teamDataApi.id.toString())

                                val memberEntities = teamDataApi.members.map { memberApi ->
                                    TeamMemberEntity(
                                        id = memberApi.id.toString(),
                                        teamId = teamDataApi.id.toString(),
                                        name = memberApi.name,
                                        email = memberApi.email,
                                        avatarUrl = memberApi.avatarUrl,
                                        role = memberApi.role
                                    )
                                }
                                teamMemberRepository.insertAllMembers(memberEntities)
                            }
                        }
                    } else {
                        loadTeamsFromLocalDatabase()
                    }
                } catch (e: Exception) {
                    loadTeamsFromLocalDatabase()
                } finally {
                    _isLoadingTeams.value = false
                }
            } else {
                loadTeamsFromLocalDatabase()
            }
        }
    }

    private fun loadTeamsFromLocalDatabase() {
        viewModelScope.launch {
            val currentUserEmail = _userData.value?.email
            if (currentUserEmail == null) {
                _userTeams.value = emptyList()
                _isLoadingTeams.value = false
                return@launch
            }

            if (!_isLoadingTeams.value) {
                _isLoadingTeams.value = true
            }

            try {
                val teamRepository = TeamRepository(getApplication())
                val teamMemberRepository = TeamMemberRepository(getApplication())

                teamMemberRepository.getTeamMembershipsForUser(currentUserEmail).collect { memberships ->
                    val teamIds = memberships.map { it.teamId }.distinct()

                    if (teamIds.isNotEmpty()) {
                        val teamsWithMembersFlows = teamIds.map { teamId ->
                            teamRepository.getTeamById(teamId).combine(teamMemberRepository.getTeamMembers(teamId)) { teamEntity, memberEntities ->
                                if (teamEntity != null) {
                                    val members = memberEntities.map { memberEntity ->
                                        TeamMemberData(
                                            id = memberEntity.id.toLong(),
                                            name = memberEntity.name,
                                            email = memberEntity.email,
                                            avatarUrl = memberEntity.avatarUrl,
                                            role = memberEntity.role
                                        )
                                    }
                                    TeamData(
                                        id = teamEntity.id.toLong(),
                                        name = teamEntity.name,
                                        department = teamEntity.department,
                                        imageUrl = teamEntity.imageUrl,
                                        members = members,
                                        projects = emptyList()
                                    )
                                } else {
                                    // If the team entity is not found, return null
                                    null
                                }
                            }
                        }
                        combine(teamsWithMembersFlows) { arrayOfTeamData ->
                            arrayOfTeamData.filterNotNull().toList()
                        }.collect { teamsWithMembersList ->
                            _userTeams.value = teamsWithMembersList
                            _isLoadingTeams.value = false
                        }
                    } else {
                        _userTeams.value = emptyList()
                        _isLoadingTeams.value = false
                    }
                }
            } catch (e: Exception) {
                _userTeams.value = emptyList()
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
                    _userTasks.value = emptyList()
                }
            } catch (e: Exception) {
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
                    _userProjects.value = emptyList()
                }
            } catch (e: Exception) {
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
                    _userObservations.value = emptyList()
                }
            } catch (e: Exception) {
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
