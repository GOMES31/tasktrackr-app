package com.example.tasktrackr_app.utils

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes

object NotificationHelper {
    fun showNotification(context: Context, @StringRes messageResource: Int) {
        Toast.makeText(context, context.getString(messageResource), Toast.LENGTH_SHORT).show()
    }
}
