package com.example.tasktrackr_app.data.local.repository

import android.content.Context
import com.example.tasktrackr_app.data.local.dao.TeamMemberDao
import com.example.tasktrackr_app.data.local.database.AppDatabase
import com.example.tasktrackr_app.data.local.entity.TeamMemberEntity
import kotlinx.coroutines.flow.Flow

class TeamMemberRepository(context: Context) {
    private val teamMemberDao = AppDatabase.getInstance(context).teamMemberDao()

    fun getTeamMembers(teamId: String): Flow<List<TeamMemberEntity>> {
        return teamMemberDao.getTeamMembers(teamId)
    }

    suspend fun insertMember(member: TeamMemberEntity) {
        teamMemberDao.insertMember(member)
    }

    suspend fun insertAllMembers(members: List<TeamMemberEntity>) {
        teamMemberDao.insertAllMembers(members)
    }

    suspend fun deleteTeamMembers(teamId: String) {
        teamMemberDao.deleteTeamMembers(teamId)
    }

    fun getTeamMembershipsForUser(userEmail: String): Flow<List<TeamMemberEntity>> {
        return teamMemberDao.getTeamMembershipsForUser(userEmail)
    }

    suspend fun getUnSyncedMembers(): List<TeamMemberEntity> {
        return teamMemberDao.getUnSyncedMembers()
    }

    suspend fun deleteMemberById(memberId: String) {
        teamMemberDao.deleteMemberById(memberId)
    }

    suspend fun updateMemberWithReplacement(member: TeamMemberEntity) {
        teamMemberDao.updateMemberWithReplacement(member)
    }
}
