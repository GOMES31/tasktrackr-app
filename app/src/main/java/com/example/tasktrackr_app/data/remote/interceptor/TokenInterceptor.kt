package com.example.tasktrackr_app.data.remote.interceptor

import android.content.Context
import android.util.Log
import androidx.navigation.NavController
import com.example.tasktrackr_app.data.local.TokenRepository
import com.example.tasktrackr_app.data.remote.api.AuthApi
import com.example.tasktrackr_app.data.remote.response.ApiResponse
import com.example.tasktrackr_app.data.remote.response.data.RefreshData
import com.example.tasktrackr_app.utils.SessionManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Interceptor
import okhttp3.Response

class TokenInterceptor(
    private val context: Context,
    private val authApi: AuthApi,
    private var navController: NavController? = null
) : Interceptor {

    fun updateNavController(newNavController: NavController?) {
        navController = newNavController
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val path = original.url.encodedPath
        val tokenRepo = TokenRepository(context.applicationContext)

        if (path.startsWith("/api/auth/signin") ||
            path.startsWith("/api/auth/signup") ||
            path.startsWith("/api/auth/refresh")){
            return chain.proceed(original)
        }

        return try {
            val accessToken = tokenRepo.getAccessToken()
            val requestWithToken = original.newBuilder().apply {
                accessToken?.let { header("Authorization", "Bearer $it") }
            }.build()

            val initialResponse = chain.proceed(requestWithToken)
            val code = initialResponse.code

            if (code != 401 && code != 403) {
                return initialResponse
            }

            if (original.header("X-Refreshed") == "true") {
                handleSignOut()
                return initialResponse
            }

            val didRefresh = refreshToken(tokenRepo)
            if (!didRefresh) {
                handleSignOut()
                return initialResponse
            }

            initialResponse.close()

            val newAccess = tokenRepo.getAccessToken()
            val retried = original.newBuilder()
                .removeHeader("Authorization")
                .header("Authorization", "Bearer $newAccess")
                .header("X-Refreshed", "true")
                .build()

            chain.proceed(retried)
        } catch (e: Exception) {
            Log.e("TokenInterceptor", "Error during intercept: ${e.message}", e)
            chain.proceed(original)
        }
    }

    private fun handleSignOut() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = authApi.signOut()
                if (!response.isSuccessful) {
                    Log.e("TokenInterceptor", "Failed to sign out on server: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("TokenInterceptor", "Error during sign out: ${e.message}", e)
            } finally {
                val tokenRepo = TokenRepository(context.applicationContext)
                tokenRepo.clearTokens()

                try {
                    SessionManager.notifySessionExpired(context, "Session expired")
                } catch (e: Exception) {
                    Log.e("TokenInterceptor", "Error notifying session expired: ${e.message}", e)
                }
            }
        }
    }

    private fun refreshToken(tokenRepo: TokenRepository): Boolean {
        return try {
            val refreshToken = tokenRepo.getRefreshToken() ?: ""
            if (refreshToken.isEmpty()) {
                Log.e("TokenInterceptor", "Refresh token is empty or null")
                return false
            }

            val call = authApi.refresh("Bearer $refreshToken")
            val response = call.execute()

            if (!response.isSuccessful) {
                Log.e("TokenInterceptor", "Refresh failed with code: ${response.code()}, message: ${response.message()}")
                tokenRepo.clearTokens()
                return false
            }

            val responseBody = response.body()
            if (responseBody == null) {
                Log.e("TokenInterceptor", "Refresh response body is null")
                tokenRepo.clearTokens()
                return false
            }

            val json = Gson().toJson(responseBody)
            val type = object : TypeToken<ApiResponse<RefreshData>>() {}.type
            val apiResponse: ApiResponse<RefreshData> = try {
                Gson().fromJson(json, type)
            } catch (e: Exception) {
                Log.e("TokenInterceptor", "Error parsing refresh response: ${e.message}", e)
                tokenRepo.clearTokens()
                return false
            }

            if (apiResponse.data == null) {
                Log.e("TokenInterceptor", "API response data is null")
                tokenRepo.clearTokens()
                return false
            }

            val newAccess = apiResponse.data.accessToken
            val newRefresh = apiResponse.data.refreshToken

            if (newAccess.isEmpty() || newRefresh.isEmpty()) {
                Log.e("TokenInterceptor", "New tokens are empty: access=${newAccess.isEmpty()}, refresh=${newRefresh.isEmpty()}")
                tokenRepo.clearTokens()
                return false
            }

            tokenRepo.saveTokens(newAccess, newRefresh)
            Log.d("TokenInterceptor", "Token refresh successful")
            true
        } catch (e: Exception) {
            Log.e("TokenInterceptor", "Exception during token refresh: ${e.message}", e)
            tokenRepo.clearTokens()
            false
        }
    }
}
