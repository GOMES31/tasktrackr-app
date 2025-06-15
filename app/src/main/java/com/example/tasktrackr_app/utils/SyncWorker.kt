package com.example.tasktrackr_app.utils

import android.annotation.SuppressLint
import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.tasktrackr_app.data.local.entity.TeamEntity
import com.example.tasktrackr_app.data.local.entity.TeamMemberEntity
import com.example.tasktrackr_app.data.local.repository.TeamMemberRepository
import com.example.tasktrackr_app.data.local.repository.TeamRepository
import com.example.tasktrackr_app.data.local.repository.UserRepository
import com.example.tasktrackr_app.data.remote.api.ApiClient
import com.example.tasktrackr_app.data.remote.request.AddTeamMemberRequest
import com.example.tasktrackr_app.data.remote.request.CreateTeamRequest
import com.example.tasktrackr_app.data.remote.request.UpdateTeamMemberRequest
import com.example.tasktrackr_app.data.remote.request.UpdateTeamRequest
import com.example.tasktrackr_app.data.remote.request.UpdateUserProfileRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    @SuppressLint("RestrictedApi")
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        syncUserProfile()
        syncTeamProfiles()
        syncTeamMembers()
    }

    private suspend fun syncUserProfile(): Result {
        val userRepository = UserRepository(applicationContext)
        val user = userRepository.getCurrentUser()

        if (user != null && !user.isSynced) {
            val userApi = ApiClient.userApi(applicationContext)
            try {
                val password = EncryptedPrefsUtil.getPassword(applicationContext)
                val request = UpdateUserProfileRequest(
                    name = user.name,
                    password = password,
                    avatarUrl = user.avatarUrl,
                    updatedAt = user.updatedAt
                )
                val response = userApi.updateProfile(request)

                if (response.isSuccessful) {
                    userRepository.insertUser(user.copy(isSynced = true))
                    if (!password.isNullOrEmpty()) {
                        EncryptedPrefsUtil.clearPassword(applicationContext)
                    }
                    return Result.success()
                } else {
                    return Result.retry()
                }
            } catch (e: Exception) {
                return Result.retry()
            }
        }
        return Result.success()
    }

    private suspend fun syncTeamProfiles(): Result {
        val teamRepository = TeamRepository(applicationContext)
        val unSyncedTeams = teamRepository.getUnSyncedTeams()

        if (unSyncedTeams.isEmpty()) {
            return Result.success()
        }

        val teamApi = ApiClient.teamApi(applicationContext)
        var allSuccess = true

        for (team in unSyncedTeams) {
            try {
                // Check if this is a new team created offline (has negative ID)

                if (team.id.startsWith("-")) {
                    val createRequest = CreateTeamRequest(
                        name = team.name,
                        department = team.department,
                        imageUrl = team.imageUrl
                    )

                    val response = teamApi.createTeam(createRequest)

                    if (response.isSuccessful) {
                        response.body()?.data?.let { newTeam ->
                            // Replace local team with id from server
                            val updatedTeam = TeamEntity(
                                id = newTeam.id.toString(),
                                name = newTeam.name,
                                department = newTeam.department,
                                imageUrl = newTeam.imageUrl,
                                isSynced = true,
                                updatedAt = System.currentTimeMillis()
                            )

                            // Delete the temporary team with negative ID
                            teamRepository.deleteTeamById(team.id)

                            // Insert the new team with server ID
                            teamRepository.insertTeam(updatedTeam)
                        }
                    } else {
                        allSuccess = false
                    }
                } else {
                    val updateRequest = UpdateTeamRequest(
                        name = team.name,
                        department = team.department,
                        imageUrl = team.imageUrl,
                        updatedAt = team.updatedAt
                    )

                    val response = teamApi.updateTeam(team.id, updateRequest)

                    if (response.isSuccessful) {
                        teamRepository.markTeamAsSynced(team.id)
                    } else {
                        allSuccess = false
                    }
                }
            } catch (e: Exception) {
                allSuccess = false
            }
        }

        return if (allSuccess) Result.success() else Result.retry()
    }

    private suspend fun syncTeamMembers(): Result {
        val teamMemberRepository = TeamMemberRepository(applicationContext)
        val teamApi = ApiClient.teamApi(applicationContext)
        var allSuccess = true

        // Get all team members that are not synced
        val unSyncedMembers = teamMemberRepository.getUnSyncedMembers()

        for (member in unSyncedMembers) {
            try {
                // Check if this is a new member created offline (negative ID")
                if (member.id.startsWith("-")) {
                    val request = AddTeamMemberRequest(
                        email = member.email,
                        role = member.role,
                        createdAt = member.createdAt
                    )

                    val response = teamApi.addMember(member.teamId, request)

                    if (response.isSuccessful) {
                        response.body()?.data?.let { newMember ->
                            // Replace local member with server data
                            val updatedMember = TeamMemberEntity(
                                id = newMember.id.toString(),
                                teamId = member.teamId,
                                name = newMember.name,
                                email = newMember.email,
                                avatarUrl = newMember.avatarUrl,
                                role = newMember.role,
                                isSynced = true,
                                updatedAt = member.updatedAt,
                                createdAt = member.createdAt
                            )

                            // Delete the temporary member
                            teamMemberRepository.deleteMemberById(member.id)

                            // Insert the new member with server ID
                            teamMemberRepository.insertMember(updatedMember)
                        }
                    } else {
                        allSuccess = false
                    }
                } else {
                    // This is an existing member that was updated offline
                    val request = UpdateTeamMemberRequest(
                        role = member.role,
                        updatedAt = member.updatedAt
                    )

                    val response = teamApi.updateMember(member.teamId, member.id.toLong(), request)

                    if (response.isSuccessful) {
                        response.body()?.data?.let {
                            teamMemberRepository.insertMember(member.copy(isSynced = true))
                        }
                    } else {
                        allSuccess = false
                    }
                }
            } catch (e: Exception) {
                allSuccess = false
            }
        }

        return if (allSuccess) Result.success() else Result.retry()
    }
}
