package com.example.tasktrackr_app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tasktrackr_app.data.local.TokenRepository
import com.example.tasktrackr_app.data.remote.api.ApiClient
import com.example.tasktrackr_app.data.remote.api.AuthApi
import com.example.tasktrackr_app.data.remote.request.SignInRequest
import com.example.tasktrackr_app.data.remote.request.SignUpRequest
import com.example.tasktrackr_app.data.remote.response.ApiResponse
import com.example.tasktrackr_app.data.remote.response.data.AuthData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Response

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    // Retrofit auth endpoints
    private val authApi: AuthApi = ApiClient.authApi(application)

    // stores access & refresh tokens
    private val tokenRepository: TokenRepository =  TokenRepository(application)

    // holds current user data
    private val _userData = MutableStateFlow<AuthData?>(null)
    val userData: StateFlow<AuthData?> = _userData.asStateFlow()

    // true after successful sign in
    private val _signInSuccess = MutableStateFlow(false)
    val signInSuccess: StateFlow<Boolean> = _signInSuccess.asStateFlow()

    // true after success sign up
    private val _signUpSuccess = MutableStateFlow(false)
    val signUpSuccess: StateFlow<Boolean> = _signUpSuccess.asStateFlow()

    private val _authenticationErrorCode = MutableStateFlow<Int?>(null)
    val errorCode: StateFlow<Int?> = _authenticationErrorCode.asStateFlow()

    // Register a new user
    fun signUp(name: String, email: String, password: String) {
        _authenticationErrorCode.value = null

        viewModelScope.launch {
            try {
                val response: Response<ApiResponse<AuthData>> = authApi.signUp(SignUpRequest(name, email, password))
                handleResponse(response, isSignUp = true)
            } catch (e: Exception) {
                _authenticationErrorCode.value = -1
            }
        }
    }

    // Login an existing user
    fun signIn(email: String, password: String) {
        _authenticationErrorCode.value = null

        viewModelScope.launch {
            try {
                val response: Response<ApiResponse<AuthData>> = authApi.signIn(SignInRequest(email, password))
                handleResponse(response, isSignUp = false)
            } catch (e: Exception) {
                _authenticationErrorCode.value = -1
            }
        }
    }

    // Handle auth response for both sign-in and sign-up
    private fun handleResponse(response: Response<ApiResponse<AuthData>>, isSignUp: Boolean) {
        if (response.isSuccessful) {
            val authData = response.body()?.data
            if (authData != null) {
                tokenRepository.saveTokens(authData.accessToken, authData.refreshToken)
                _userData.value = authData

                if (isSignUp) {
                    _signUpSuccess.value = true
                } else {
                    _signInSuccess.value = true
                }
            } else {
                _authenticationErrorCode.value = response.code()
            }
        } else {
            _authenticationErrorCode.value = response.code()
        }
    }
}
