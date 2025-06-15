package com.example.tasktrackr_app.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class TokenRepository(context: Context) {
    companion object {
        private const val PREFS_NAME = "token_prefs"
        private const val KEY_ACCESS = "access_token"
        private const val KEY_REFRESH = "refresh_token"
    }

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getAccessToken(): String? = prefs.getString(KEY_ACCESS, null)

    fun getRefreshToken(): String? = prefs.getString(KEY_REFRESH, null)

    fun saveTokens(accessToken: String, refreshToken: String) {
        prefs.edit {
            putString(KEY_ACCESS, accessToken)
            putString(KEY_REFRESH, refreshToken)
            .apply()
        }
    }

    fun clearTokens() {
        prefs.edit {
            remove(KEY_ACCESS)
            remove(KEY_REFRESH)
            .apply()
        }
    }
}

