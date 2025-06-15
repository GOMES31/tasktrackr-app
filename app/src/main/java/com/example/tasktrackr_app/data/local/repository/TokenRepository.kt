package com.example.tasktrackr_app.data.local.repository

import android.content.Context
import com.example.tasktrackr_app.data.local.database.AppDatabase
import com.example.tasktrackr_app.data.local.entity.TokenEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TokenRepository(context: Context) {
    private val tokenDao = AppDatabase.getInstance(context).tokenDao()

    suspend fun getAccessToken(): String? = withContext(Dispatchers.IO) {
        tokenDao.getToken()?.accessToken
    }

    suspend fun getRefreshToken(): String? = withContext(Dispatchers.IO) {
        tokenDao.getToken()?.refreshToken
    }

    suspend fun saveTokens(accessToken: String, refreshToken: String) = withContext(Dispatchers.IO) {
        tokenDao.insertToken(TokenEntity(accessToken = accessToken, refreshToken = refreshToken))
    }

    suspend fun clearTokens() = withContext(Dispatchers.IO) {
        tokenDao.clearToken()
    }
}
