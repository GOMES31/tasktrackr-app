package com.example.tasktrackr_app.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class AuthRepository(context: Context) {
    companion object {
        private const val PREFS_NAME  = "jwt_tokens"
        private const val KEY_ACCESS  = "ACCESS_TOKEN"
        private const val KEY_REFRESH = "REFRESH_TOKEN"
    }

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun saveTokens(accessToken: String, refreshToken: String) {
        prefs.edit {
            putString(KEY_ACCESS, accessToken)
                .putString(KEY_REFRESH, refreshToken)
        }
    }

    fun getAccessToken(): String? =
        prefs.getString(KEY_ACCESS, null)

    fun getRefreshToken(): String? =
        prefs.getString(KEY_REFRESH, null)

    fun clearTokens() {
        prefs.edit { clear() }
    }
}
