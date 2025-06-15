package com.example.tasktrackr_app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.tasktrackr_app.data.local.entity.TeamEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TeamDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(team: TeamEntity)

    @Update
    suspend fun update(team: TeamEntity)

    @Query("SELECT * FROM teams WHERE id = :teamId")
    fun getTeamById(teamId: String): Flow<TeamEntity?>

    @Query("SELECT * FROM teams")
    fun getAllTeams(): Flow<List<TeamEntity>>

    @Query("SELECT * FROM teams WHERE isSynced = 0")
    suspend fun getUnSyncedTeams(): List<TeamEntity>

    @Query("UPDATE teams SET isSynced = 1 WHERE id = :teamId")
    suspend fun markAsSynced(teamId: String)

    @Query("DELETE FROM teams WHERE id = :teamId")
    suspend fun deleteById(teamId: String)
}
