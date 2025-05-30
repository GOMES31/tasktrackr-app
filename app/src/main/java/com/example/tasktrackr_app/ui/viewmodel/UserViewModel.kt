package com.example.tasktrackr_app.ui.viewmodel

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tasktrackr_app.data.local.TokenRepository
import com.example.tasktrackr_app.data.remote.api.ApiClient
import com.example.tasktrackr_app.data.remote.api.UserApi
import com.example.tasktrackr_app.data.remote.request.UpdateUserProfileRequest
import com.example.tasktrackr_app.data.remote.response.ApiResponse
import com.example.tasktrackr_app.data.remote.response.data.AuthData
import com.example.tasktrackr_app.utils.LocalImageStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Response

class UserViewModel(application: Application) : AndroidViewModel(application) {
    private val userApi: UserApi = ApiClient.userApi(application)
    private val tokenRepository: TokenRepository = TokenRepository(application)
    private val _userData = MutableStateFlow<AuthData?>(null)
    val userData = _userData.asStateFlow()
    private val _errorCode = MutableStateFlow<Int?>(null)

    fun loadProfile(data: AuthData) {
        _userData.value = data
    }

    fun updateProfile(name: String?, password: String?, avatarUri: Uri?) {
        _errorCode.value = null
        viewModelScope.launch {
            try {
                val localAvatarPath = if (avatarUri != null) {
                    LocalImageStorage.saveProfileImage(getApplication(), avatarUri)?.also {
                        Log.d("UserViewModel", "Saved image to local path: $it")
                    }
                } else {
                    userData.value?.avatarUrl // Mantém o avatar atual se não houver nova imagem
                }

                val request = UpdateUserProfileRequest(
                    name = name ?: userData.value?.name,
                    password = password,
                    avatarUrl = localAvatarPath
                )

                Log.d("UserViewModel", "Sending update request with data: $request")
                val response: Response<ApiResponse<AuthData>> = userApi.updateProfile(request)

                Log.d("UserViewModel", "Raw response from backend: ${response.raw()}")
                Log.d("UserViewModel", "Response code: ${response.code()}")
                Log.d("UserViewModel", "Response headers: ${response.headers()}")

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    Log.d("UserViewModel", "Response body: $responseBody")
                    val updatedData = responseBody?.data
                    if (updatedData != null) {
                        Log.d("UserViewModel", "Profile updated successfully with data: $updatedData")
                        _userData.value = updatedData
                    } else {
                        Log.e("UserViewModel", "Response body data is null")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("UserViewModel", "Failed to update profile: ${response.code()}, Error: $errorBody")
                    _errorCode.value = response.code()
                }
            } catch (e: Exception) {
                _errorCode.value = -1
                Log.e("UserViewModel", "Error updating profile", e)
            }
        }
    }
}
