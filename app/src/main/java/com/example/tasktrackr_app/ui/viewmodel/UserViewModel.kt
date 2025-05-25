package com.example.tasktrackr_app.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tasktrackr_app.data.local.TokenRepository
import com.example.tasktrackr_app.data.remote.api.ApiClient
import com.example.tasktrackr_app.data.remote.api.UserApi
import com.example.tasktrackr_app.data.remote.request.UpdateUserProfileRequest
import com.example.tasktrackr_app.data.remote.response.ApiResponse
import com.example.tasktrackr_app.data.remote.response.data.AuthData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Response

class UserViewModel(application: Application) : AndroidViewModel(application) {
    private val userApi: UserApi = ApiClient.userApi(application)

    private val tokenRepository: TokenRepository =  TokenRepository(application)

    private val _userData = MutableStateFlow<AuthData?>(null)
    val userData = _userData.asStateFlow()

    private val _errorCode = MutableStateFlow<Int?>(null)

    fun loadProfile(data: AuthData) {
        _userData.value = data
    }

    fun updateProfile(name: String?, password: String?, avatarUrl: String?) {
        _errorCode.value = null
        viewModelScope.launch {
            try {
                val request = UpdateUserProfileRequest(name, password, avatarUrl)
                val response: Response<ApiResponse<AuthData>> = userApi.updateProfile(request)
                if (response.isSuccessful) {
                    val updatedData = response.body()?.data
                    if (updatedData != null) {
                        _userData.value = updatedData
                        tokenRepository.saveTokens(updatedData.accessToken, updatedData.refreshToken)
                    }
                } else {
                    _errorCode.value = response.code()
                }
            } catch (t: Throwable) {
                _errorCode.value = -1
                Log.e("UserViewModel", "updateProfile error", t)
            }
        }
    }
}
