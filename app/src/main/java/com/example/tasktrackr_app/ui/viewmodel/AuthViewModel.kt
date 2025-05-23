package com.example.tasktrackr_app.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tasktrackr_app.data.local.AuthRepository
import com.example.tasktrackr_app.data.remote.request.SignInRequest
import com.example.tasktrackr_app.data.remote.request.SignUpRequest
import com.example.tasktrackr_app.data.remote.response.AuthResponse
import com.example.tasktrackr_app.data.remote.response.AuthResponse.Data
import com.example.tasktrackr_app.data.remote.api.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Response

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val authRepo = AuthRepository(application)
    private val authApi  = ApiClient.authApi(application)

    private val _signUpSuccess = MutableStateFlow(false)
    val signUpSuccess = _signUpSuccess.asStateFlow()

    private val _signInSuccess = MutableStateFlow(false)
    val signInSuccess = _signInSuccess.asStateFlow()

    private val _errorCode = MutableStateFlow<Int?>(null)
    val errorCode = _errorCode.asStateFlow()

    private val _userEmail = MutableStateFlow<String?>(null)
    val userEmail = _userEmail.asStateFlow()

    fun signUp(name: String, email: String, password: String) {
        _errorCode.value = null
        viewModelScope.launch {
            try {
                val resp: Response<AuthResponse> = authApi.signUp(
                    SignUpRequest(name = name, email = email, password = password)
                )
                Log.d("AUTH", "SignUp response code: ${resp.code()}")
                if (resp.isSuccessful) {
                    resp.body()?.data?.let { data ->
                        authRepo.saveTokens(data.accessToken, data.refreshToken)
                        Log.d("AUTH", "Tokens saved: access=${data.accessToken}, refresh=${data.refreshToken}")
                        _userEmail.value = data.email
                        _signUpSuccess.value = true
                    }
                }
            } catch (e: Exception) {
                Log.e("AUTH", "Exception during signUp", e)
                _errorCode.value = -1
            }
        }
    }

    fun signIn(email: String, password: String) {
        _errorCode.value = null
        viewModelScope.launch {
            try {
                val resp: Response<AuthResponse> = authApi.signIn(
                    SignInRequest(email = email, password = password)
                )
                Log.d("AUTH", "SignIn response code: ${resp.code()}")
                if (resp.isSuccessful) {
                    resp.body()?.data?.let { data ->
                        authRepo.saveTokens(data.accessToken, data.refreshToken)
                        Log.d("AUTH", "Tokens saved: access=${data.accessToken}, refresh=${data.refreshToken}")
                        _userEmail.value = data.email
                        _signInSuccess.value = true
                    }
                } else {
                    Log.e("AUTH", "SignIn failed with code: ${resp.code()}")
                    _errorCode.value = resp.code()
                }
            } catch (e: Exception) {
                Log.e("AUTH", "Exception during signIn", e)
                _errorCode.value = -1
            }
        }
    }
}
