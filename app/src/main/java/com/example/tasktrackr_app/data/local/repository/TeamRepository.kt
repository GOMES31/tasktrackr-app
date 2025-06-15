package com.example.tasktrackr_app.data.local.repository

import android.content.Context
import com.example.tasktrackr_app.data.local.dao.TeamDao
import com.example.tasktrackr_app.data.local.database.AppDatabase
import com.example.tasktrackr_app.data.local.entity.TeamEntity
import kotlinx.coroutines.flow.Flow

class TeamRepository(context: Context){

    private val teamDao = AppDatabase.getInstance(context).teamDao()

    fun getAllTeams(): Flow<List<TeamEntity>> {
        return teamDao.getAllTeams()
    }

    fun getTeamById(teamId: String): Flow<TeamEntity?> {
        return teamDao.getTeamById(teamId)
    }

    suspend fun insertTeam(team: TeamEntity) {
        teamDao.insert(team)
    }

    suspend fun updateTeam(team: TeamEntity) {
        teamDao.update(team)
    }

    suspend fun getUnSyncedTeams(): List<TeamEntity> {
        return teamDao.getUnSyncedTeams()
    }

    suspend fun markTeamAsSynced(teamId: String) {
        teamDao.markAsSynced(teamId)
    }

    suspend fun deleteTeamById(teamId: String) {
        teamDao.deleteById(teamId)
    }
}
