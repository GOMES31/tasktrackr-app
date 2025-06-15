package com.example.tasktrackr_app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.tasktrackr_app.data.local.entity.TeamMemberEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TeamMemberDao {
    @Query("SELECT * FROM team_members WHERE teamId = :teamId")
    fun getTeamMembers(teamId: String): Flow<List<TeamMemberEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMember(member: TeamMemberEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllMembers(members: List<TeamMemberEntity>)

    @Query("DELETE FROM team_members WHERE teamId = :teamId")
    suspend fun deleteTeamMembers(teamId: String)

    @Query("SELECT * FROM team_members WHERE email = :userEmail")
    fun getTeamMembershipsForUser(userEmail: String): Flow<List<TeamMemberEntity>>

    @Query("SELECT * FROM team_members WHERE isSynced = 0")
    suspend fun getUnSyncedMembers(): List<TeamMemberEntity>

    @Query("DELETE FROM team_members WHERE id = :memberId")
    suspend fun deleteMemberById(memberId: String)

    @Transaction
    suspend fun updateMemberWithReplacement(member: TeamMemberEntity) {
        deleteMemberById(member.id)
        insertMember(member)
    }
}
