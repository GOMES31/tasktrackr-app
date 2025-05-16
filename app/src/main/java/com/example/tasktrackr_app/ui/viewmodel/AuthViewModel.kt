package com.example.tasktrackr_app.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tasktrackr_app.data.remote.request.SignInRequest
import com.example.tasktrackr_app.data.remote.request.SignUpRequest
import com.example.tasktrackr_app.data.remote.response.AuthResponse
import com.example.tasktrackr_app.data.remote.api.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Response

class AuthViewModel : ViewModel() {

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
                val resp: Response<AuthResponse> = ApiClient.authApi.signUp(
                    SignUpRequest(name = name, email = email, password = password)
                )
                if (resp.isSuccessful) {
                    val body = resp.body()
                    body?.email?.let { _userEmail.value = it }
                    _signUpSuccess.value = true
                } else {
                    _errorCode.value = resp.code()
                }
            } catch (e: Exception) {
                _errorCode.value = -1
            }
        }
    }

    fun signIn(email: String, password: String) {
        _errorCode.value = null
        viewModelScope.launch {
            try {
                val resp: Response<AuthResponse> = ApiClient.authApi.signIn(
                    SignInRequest(email = email, password = password)
                )
                Log.d("SIGN-IN","signIn response code: ${resp.code()}")
                if (resp.isSuccessful) {
                    val body = resp.body()
                    Log.d("SIGN-IN","signIn body: ${body}")
                    body?.email?.let { _userEmail.value = it }
                    _signInSuccess.value = true
                } else {
                    Log.e("SIGN-IN", "signIn failed: ${resp.errorBody()?.string()}")
                    _errorCode.value = resp.code()
                }
            } catch (e: Exception) {
                Log.e("SIGN-IN", "signIn exception", e)
                _errorCode.value = -1
            }
        }
    }
}
