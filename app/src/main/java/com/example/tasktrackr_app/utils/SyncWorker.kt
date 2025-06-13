package com.example.tasktrackr_app.utils

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.NetworkType
import androidx.work.Constraints
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.tasktrackr_app.data.local.repository.UserRepository
import com.example.tasktrackr_app.data.remote.api.ApiClient
import com.example.tasktrackr_app.data.remote.request.UpdateUserProfileRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        syncUserProfile()
    }

    private suspend fun syncUserProfile(): Result {
        val userRepository = UserRepository(applicationContext)
        val user = userRepository.getCurrentUser()

        if (user != null && !user.isSynced) {
            val userApi = ApiClient.userApi(applicationContext)
            try {
                val password = EncryptedPrefsUtil.getPassword(applicationContext)
                val request = UpdateUserProfileRequest(
                    name = user.name,
                    password = password,
                    avatarUrl = user.avatarUrl,
                    updatedAt = user.updatedAt
                )
                val response = userApi.updateProfile(request)

                if (response.isSuccessful) {
                    userRepository.insertUser(user.copy(isSynced = true))
                    if (!password.isNullOrEmpty()) {
                        EncryptedPrefsUtil.clearPassword(applicationContext)
                    }
                    return Result.success()
                } else {
                    return Result.retry()
                }
            } catch (e: Exception) {
                return Result.retry()
            }
        }
        return Result.success()
    }
}
