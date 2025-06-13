package com.example.tasktrackr_app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "token")
data class TokenEntity(
    @PrimaryKey val id: Int = 0,
    val accessToken: String?,
    val refreshToken: String?
)

