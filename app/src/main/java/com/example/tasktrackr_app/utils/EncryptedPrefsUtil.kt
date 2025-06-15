package com.example.tasktrackr_app.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

object EncryptedPrefsUtil {
    private const val PREFS_NAME = "secure_prefs"
    private const val KEY_PASSWORD = "user_password"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun savePassword(context: Context, password: String) {
        getPrefs(context).edit { putString(KEY_PASSWORD, password) }
    }

    fun getPassword(context: Context): String? {
        return getPrefs(context).getString(KEY_PASSWORD, null)
    }

    fun clearPassword(context: Context) {
        getPrefs(context).edit { remove(KEY_PASSWORD) }
    }
}

