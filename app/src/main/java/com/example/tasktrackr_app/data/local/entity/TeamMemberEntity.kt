package com.example.tasktrackr_app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "team_members",
    foreignKeys = [
        ForeignKey(
            entity = TeamEntity::class,
            parentColumns = ["id"],
            childColumns = ["teamId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class TeamMemberEntity(
    @PrimaryKey val id: String,
    val teamId: String,
    val name: String,
    val email: String,
    val avatarUrl: String?,
    val role: String,
    val isSynced: Boolean = true,
    val updatedAt: Long = System.currentTimeMillis(),
    val createdAt : Long = System.currentTimeMillis()
)

