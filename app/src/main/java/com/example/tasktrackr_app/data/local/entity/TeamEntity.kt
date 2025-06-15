package com.example.tasktrackr_app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.tasktrackr_app.data.local.converter.ListConverter

@Entity(tableName = "teams")
@TypeConverters(ListConverter::class)
data class TeamEntity(
    @PrimaryKey val id: String,
    val name: String,
    val department: String,
    val imageUrl: String?,
    val isSynced: Boolean = true,
    val updatedAt: Long = System.currentTimeMillis()
)
