package com.example.tasktrackr_app.data.local.repository

import android.content.Context
import androidx.room.Room
import com.example.tasktrackr_app.data.local.database.AppDatabase
import com.example.tasktrackr_app.data.local.entity.UserEntity
import com.example.tasktrackr_app.data.remote.api.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepository(context: Context) {

    private val userDao = AppDatabase.getInstance(context).userDao()

    suspend fun insertUser(user: UserEntity) = withContext(Dispatchers.IO) {
        userDao.insertUser(user)
    }


    suspend fun getCurrentUser(): UserEntity? = withContext(Dispatchers.IO) {
        userDao.getCurrentUser()
    }

    suspend fun deleteUser(email: String) = withContext(Dispatchers.IO) {
        userDao.deleteUser(email)
    }
}
