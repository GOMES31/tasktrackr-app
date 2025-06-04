package com.example.tasktrackr_app.ui.viewmodel

import android.app.Application
import android.util.Log
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

    private val authApi: AuthApi = ApiClient.authApi(application)

    // access & refresh tokens
    private val tokenRepository: TokenRepository =  TokenRepository(application)

    private val _userData = MutableStateFlow<AuthData?>(null)
    val userData: StateFlow<AuthData?> = _userData.asStateFlow()

    // true after successful sign in
    private val _signInSuccess = MutableStateFlow(false)
    val signInSuccess: StateFlow<Boolean> = _signInSuccess.asStateFlow()

    // true after success sign up
    private val _signUpSuccess = MutableStateFlow(false)
    val signUpSuccess = _signUpSuccess.asStateFlow()

    private val _authenticationErrorCode = MutableStateFlow<Int?>(null)
    val errorCode: StateFlow<Int?> = _authenticationErrorCode.asStateFlow()


    // Register a new user
    fun signUp(name: String, email: String, password: String) {
        viewModelScope.launch {
            try {
                val response = authApi.signUp(SignUpRequest(name, email, password))

                if (response.isSuccessful) {
                    response.body()?.data?.let { auth ->
                        _userData.value = auth
                        _signUpSuccess.value = true
                    }
                } else {
                    _authenticationErrorCode.value = response.code()
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Error during sign up", e)
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
                handleResponse(response, false)
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

    fun signOut(onSignOutComplete: (() -> Unit)? = null) {
        viewModelScope.launch {
            try {
                val response = authApi.signOut()
                if (response.isSuccessful) {
                    clearData()
                    onSignOutComplete?.invoke()
                } else {
                    clearData()
                    onSignOutComplete?.invoke()
                }
            } catch (e: Exception) {
                clearData()
                onSignOutComplete?.invoke()
            }
        }
    }

    private fun clearData() {
        tokenRepository.clearTokens()
        _userData.value = null
        _signInSuccess.value = false
        _signUpSuccess.value = false
        _authenticationErrorCode.value = null
    }

    fun resetSignUpSuccess() {
        _signUpSuccess.value = false
    }
}
