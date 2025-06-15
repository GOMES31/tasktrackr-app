package com.example.tasktrackr_app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val email: String,
    val name: String,
    val avatarUrl: String?,
    val isSynced: Boolean = true,
    val updatedAt: Long = System.currentTimeMillis()
)
