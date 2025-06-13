package com.example.tasktrackr_app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.tasktrackr_app.data.local.entity.TokenEntity

@Dao
interface TokenDao {
    @Query("SELECT * FROM token WHERE id = 0 LIMIT 1")
    suspend fun getToken(): TokenEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertToken(token: TokenEntity)

    @Query("DELETE FROM token WHERE id = 0")
    suspend fun clearToken()
}
