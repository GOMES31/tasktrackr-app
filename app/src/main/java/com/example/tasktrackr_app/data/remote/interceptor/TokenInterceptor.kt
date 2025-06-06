package com.example.tasktrackr_app.data.remote.interceptor

import android.content.Context
import android.util.Log
import com.example.tasktrackr_app.data.local.TokenRepository
import com.example.tasktrackr_app.data.remote.api.AuthApi
import com.example.tasktrackr_app.data.remote.response.ApiResponse
import com.example.tasktrackr_app.data.remote.response.data.RefreshData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.Interceptor
import okhttp3.Response

class TokenInterceptor(
    private val context: Context,
    private val authApi: AuthApi
) : Interceptor {

    private val TAG = "TokenInterceptor"

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val path = original.url.encodedPath
        val tokenRepo = TokenRepository(context.applicationContext)

        if (path.startsWith("/api/auth/")) {
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
                return initialResponse
            }

            val didRefresh = refreshToken(tokenRepo)
            if (!didRefresh) {
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
            Log.e(TAG, "Interceptor error: ${e.message}", e)
            chain.proceed(original)
        }
    }

    private fun refreshToken(tokenRepo: TokenRepository): Boolean {
        return try {
            val refreshToken = tokenRepo.getRefreshToken() ?: ""
            if (refreshToken.isEmpty()) {
                Log.d(TAG, "No refresh token")
                return false
            }

            val call = authApi.refresh("Bearer $refreshToken")
            val response = call.execute()
            val code = response.code()

            if (!response.isSuccessful) {
                val body = response.errorBody()?.string()
                Log.e(TAG, "Refresh failed ($code), body: $body")
                tokenRepo.clearTokens()
                return false
            }

            val responseBody = response.body()
            if (responseBody == null) {
                Log.e(TAG, "Empty refresh response")
                tokenRepo.clearTokens()
                return false
            }

            val json = Gson().toJson(responseBody)
            val type = object : TypeToken<ApiResponse<RefreshData>>() {}.type
            val apiResponse: ApiResponse<RefreshData> = try {
                Gson().fromJson(json, type)
            } catch (e: Exception) {
                Log.e(TAG, "JSON parse error: ${e.message}")
                tokenRepo.clearTokens()
                return false
            }

            val newAccess = apiResponse.data.accessToken
            val newRefresh = apiResponse.data.refreshToken
            if (newAccess.isEmpty() || newRefresh.isEmpty()) {
                Log.e(TAG, "Invalid tokens in response")
                tokenRepo.clearTokens()
                return false
            }

            tokenRepo.saveTokens(newAccess, newRefresh)
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error during refresh: ${e.message}")
            false
        }
    }
}
