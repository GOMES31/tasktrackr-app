package com.example.tasktrackr_app.ui.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tasktrackr_app.data.local.TokenRepository
import com.example.tasktrackr_app.data.local.entity.TeamEntity
import com.example.tasktrackr_app.data.local.entity.TeamMemberEntity
import com.example.tasktrackr_app.data.local.repository.TeamMemberRepository
import com.example.tasktrackr_app.data.local.repository.TeamRepository
import com.example.tasktrackr_app.data.remote.api.ApiClient
import com.example.tasktrackr_app.data.remote.api.TeamApi
import com.example.tasktrackr_app.data.remote.request.AddTeamMemberRequest
import com.example.tasktrackr_app.data.remote.request.CreateTeamRequest
import com.example.tasktrackr_app.data.remote.request.UpdateTeamMemberRequest
import com.example.tasktrackr_app.data.remote.request.UpdateTeamRequest
import com.example.tasktrackr_app.data.remote.response.data.TeamData
import com.example.tasktrackr_app.data.remote.response.data.TeamMemberData
import com.example.tasktrackr_app.utils.LocalImageStorage
import com.example.tasktrackr_app.utils.NetworkChangeReceiver
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class TeamViewModel(application: Application) : AndroidViewModel(application) {
    private val teamApi: TeamApi = ApiClient.teamApi(application)
    private val teamRepository: TeamRepository = TeamRepository(application)
    private val teamMemberRepository: TeamMemberRepository = TeamMemberRepository(application)


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
                    LocalImageStorage.saveTeamLogo(getApplication(), uri)
                }

                // Check if connected to WiFi
                if (NetworkChangeReceiver.isWifiConnected(getApplication())) {
                    val request = CreateTeamRequest(
                        name = name,
                        department = department,
                        imageUrl = logoPath
                    )

                    try {
                        val response = teamApi.createTeam(request)

                        if (response.isSuccessful) {

                            // Save to local database
                            response.body()?.data?.let { teamData ->
                                val teamEntity = TeamEntity(
                                    id = teamData.id.toString(),
                                    name = teamData.name,
                                    department = teamData.department,
                                    imageUrl = teamData.imageUrl,
                                    isSynced = true,
                                    updatedAt = System.currentTimeMillis()
                                )
                                teamRepository.insertTeam(teamEntity)
                            }
                            _createTeamSuccess.value = true
                        } else {
                            _errorCode.value = response.code()
                        }
                    } catch (e: Exception) {
                        _errorCode.value = -1
                    }
                } else {

                    // Create a temporary team entity with a negative ID
                    val tempId = -(System.currentTimeMillis())
                    val teamEntity = TeamEntity(
                        id = tempId.toString(),
                        name = name,
                        department = department,
                        imageUrl = logoPath,
                        isSynced = false,
                        updatedAt = System.currentTimeMillis()
                    )
                    teamRepository.insertTeam(teamEntity)
                    _createTeamSuccess.value = true
                }
            } catch (e: Exception) {
                _errorCode.value = -1
            }
        }
    }

    fun loadTeam(teamId: String) {
        viewModelScope.launch {
            try {
                // Check if connected to WiFi
                if (NetworkChangeReceiver.isWifiConnected(getApplication())) {
                    val response = teamApi.getTeam(teamId)
                    if (response.isSuccessful) {
                        response.body()?.data?.let { team ->
                            _selectedTeam.value = team

                            // Save team to local database
                            val teamEntity = TeamEntity(
                                id = team.id.toString(),
                                name = team.name,
                                department = team.department,
                                imageUrl = team.imageUrl,
                                isSynced = true,
                                updatedAt = System.currentTimeMillis()
                            )
                            teamRepository.insertTeam(teamEntity)

                            // Save team members to local database
                            val teamMemberRepository = TeamMemberRepository(getApplication())

                            // Delete existing members to avoid duplicates
                            teamMemberRepository.deleteTeamMembers(teamId)

                            // Then insert the current members
                            val memberEntities = team.members.map { member ->
                                TeamMemberEntity(
                                    id = member.id.toString(),
                                    teamId = teamId,
                                    name = member.name,
                                    email = member.email,
                                    avatarUrl = member.avatarUrl,
                                    role = member.role
                                )
                            }

                            if (memberEntities.isNotEmpty()) {
                                teamMemberRepository.insertAllMembers(memberEntities)
                            }
                        }
                    } else {
                        _errorCode.value = response.code()
                        // Try to load from local database if API fails
                        loadTeamFromLocalDatabase(teamId)
                    }
                } else {
                    // Load team from local database
                    loadTeamFromLocalDatabase(teamId)
                }
            } catch (e: Exception) {
                _errorCode.value = -1
                loadTeamFromLocalDatabase(teamId)
            }
        }
    }

    private fun loadTeamFromLocalDatabase(teamId: String) {
        viewModelScope.launch {
            try {
                val teamEntityFlow = teamRepository.getTeamById(teamId)
                val teamMemberRepository = TeamMemberRepository(getApplication())
                val memberEntitiesFlow = teamMemberRepository.getTeamMembers(teamId)

                teamEntityFlow.combine(memberEntitiesFlow) { teamEntity, memberEntities ->
                    if (teamEntity != null) {
                        val members = memberEntities.map { entity ->
                            TeamMemberData(
                                id = entity.id.toLong(),
                                name = entity.name,
                                email = entity.email,
                                avatarUrl = entity.avatarUrl,
                                role = entity.role
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
                        // Team entity itself is not found
                        null
                    }
                }.collect { combinedTeamData ->
                    if (combinedTeamData != null) {
                        _selectedTeam.value = combinedTeamData
                    } else {
                        _selectedTeam.value = null
                        _errorCode.value = 404 // Team not found
                    }
                }
            } catch (e: Exception) {
                _selectedTeam.value = null
                _errorCode.value = -1 // Generic error
            }
        }
    }

    fun updateTeam(teamId: String, name: String?, department: String?, logoUri: Uri?) {
        viewModelScope.launch {
            try {
                val logoPath = logoUri?.let { uri ->
                    LocalImageStorage.saveTeamLogo(getApplication(), uri)
                } ?: _selectedTeam.value?.imageUrl

                val currentTime = System.currentTimeMillis()

                // Create updated team entity
                val teamEntity = TeamEntity(
                    id = teamId,
                    name = name ?: _selectedTeam.value?.name ?: "",
                    department = department ?: _selectedTeam.value?.department ?: "",
                    imageUrl = logoPath,
                    isSynced = NetworkChangeReceiver.isWifiConnected(getApplication()),
                    updatedAt = currentTime
                )

                // Save team to local database
                teamRepository.updateTeam(teamEntity)

                // Update UI with local data
                _selectedTeam.value = _selectedTeam.value?.copy(
                    name = name ?: _selectedTeam.value?.name ?: "",
                    department = department ?: _selectedTeam.value?.department ?: "",
                    imageUrl = logoPath,
                    projects = _selectedTeam.value?.projects ?: emptyList()
                )

                // If connected to WiFi, sync with server
                if (NetworkChangeReceiver.isWifiConnected(getApplication())) {
                    try {
                        val request = UpdateTeamRequest(
                            name = name,
                            department = department,
                            imageUrl = logoPath,
                            updatedAt = currentTime
                        )

                        val response = teamApi.updateTeam(teamId, request)

                        if (response.isSuccessful) {
                            response.body()?.data?.let { team ->
                                _selectedTeam.value = team

                                // Mark as synced in local database
                                teamRepository.markTeamAsSynced(teamId)
                            }
                        } else {
                            _errorCode.value = response.code()
                        }
                    } catch (e: Exception) {
                        _errorCode.value = -1
                    }
                }

                _updateTeamSuccess.value = true
            } catch (e: Exception) {
                _errorCode.value = -1
            }
        }
    }

    fun removeMember(teamId: String, memberId: Long) {
        viewModelScope.launch {

            try {
                val response = teamApi.removeMember(teamId, memberId)

                if (response.isSuccessful) {
                    loadTeam(teamId)
                    _memberRemoved.value = true
                } else {
                    _errorCode.value = response.code()
                }
            } catch (e: Exception) {
                _errorCode.value = -1
            }
        }
    }

    fun addMember(teamId: String, email: String, role: String) {
        viewModelScope.launch {
            try {
                val currentTime = System.currentTimeMillis()

                val tempId = -(System.currentTimeMillis())


                if (NetworkChangeReceiver.isWifiConnected(getApplication())) {
                    val request = AddTeamMemberRequest(
                        email = email,
                        role = role,
                        createdAt = currentTime
                    )

                    try {
                        val response = teamApi.addMember(teamId, request)
                        if (response.isSuccessful) {
                            response.body()?.data?.let { serverMember ->
                                val memberEntity = TeamMemberEntity(
                                    id = serverMember.id.toString(),
                                    teamId = teamId,
                                    name = serverMember.name,
                                    email = serverMember.email,
                                    avatarUrl = serverMember.avatarUrl,
                                    role = serverMember.role,
                                    isSynced = true,
                                    updatedAt = currentTime,
                                    createdAt = currentTime
                                )
                                teamMemberRepository.insertMember(memberEntity)
                                _addMemberSuccess.value = true
                            }
                        }
                    } catch (e: Exception) {
                        // Network error, store locally

                        val tempMemberData = TeamMemberData(
                            id = tempId,
                            name = email.substringBefore('@'),
                            email = email,
                            avatarUrl = null,
                            role = role
                        )

                        val memberEntity = TeamMemberEntity(
                            id = tempMemberData.id.toString(),
                            teamId = teamId,
                            name = tempMemberData.name,
                            email = tempMemberData.email,
                            avatarUrl = tempMemberData.avatarUrl,
                            role = tempMemberData.role,
                            isSynced = false,
                            updatedAt = currentTime,
                            createdAt = currentTime
                        )
                        teamMemberRepository.insertMember(memberEntity)
                        _errorCode.value = -1
                    }
                } else {
                    // Offline mode, store locally

                    val tempMemberData = TeamMemberData(
                        id = tempId,
                        name = email.substringBefore('@'),
                        email = email,
                        avatarUrl = null,
                        role = role
                    )

                    val memberEntity = TeamMemberEntity(
                        id = tempMemberData.id.toString(),
                        teamId = teamId,
                        name = tempMemberData.name,
                        email = tempMemberData.email,
                        avatarUrl = tempMemberData.avatarUrl,
                        role = tempMemberData.role,
                        isSynced = false,
                        updatedAt = currentTime,
                        createdAt = currentTime
                    )
                    teamMemberRepository.insertMember(memberEntity)
                    _addMemberSuccess.value = true
                }

                loadTeam(teamId)
            } catch (e: Exception) {
                _errorCode.value = -1
            }
        }
    }

    fun updateMember(teamId: String, memberId: Long, role: String) {
        viewModelScope.launch {
            try {
                val currentTime = System.currentTimeMillis()
                val existingMember = _selectedTeam.value?.members?.find { it.id == memberId }

                // Create and save updated member entity to local database
                val memberEntity = TeamMemberEntity(
                    id = memberId.toString(),
                    teamId = teamId,
                    name = existingMember?.name ?: "",
                    email = existingMember?.email ?: "",
                    avatarUrl = existingMember?.avatarUrl,
                    role = role,
                    isSynced = NetworkChangeReceiver.isWifiConnected(getApplication()),
                    updatedAt = currentTime
                )
                teamMemberRepository.updateMemberWithReplacement(memberEntity)

                // If connected to WiFi, sync with server
                if (NetworkChangeReceiver.isWifiConnected(getApplication())) {
                    try {
                        val request = UpdateTeamMemberRequest(
                            role = role,
                            updatedAt = currentTime
                        )

                        val response = teamApi.updateMember(teamId, memberId, request)
                        if (response.isSuccessful) {
                            response.body()?.data?.let { serverMember ->
                                val updatedEntity = memberEntity.copy(
                                    name = serverMember.name,
                                    email = serverMember.email,
                                    avatarUrl = serverMember.avatarUrl,
                                    isSynced = true
                                )
                                teamMemberRepository.updateMemberWithReplacement(updatedEntity)
                            }
                        } else {
                            _errorCode.value = response.code()
                        }
                    } catch (e: Exception) {
                        _errorCode.value = -1
                    }
                }

                loadTeam(teamId)
                _updateMemberSuccess.value = true
            } catch (e: Exception) {
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
