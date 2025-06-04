package com.example.tasktrackr_app.utils

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

object NotificationHelper {
    // Toast message data
    private var toastMessage = mutableStateOf("")
    private var isToastVisible = mutableStateOf(false)
    private var isSuccess = mutableStateOf(true)

    // Getter for toast state
    val message: MutableState<String> get() = toastMessage
    val visible: MutableState<Boolean> get() = isToastVisible
    val success: MutableState<Boolean> get() = isSuccess

    // Success notifications
    private fun showSuccess(context: Context, @StringRes messageResource: Int) {
        toastMessage.value = context.getString(messageResource)
        isSuccess.value = true
        isToastVisible.value = true
    }

    // Error notifications
    private fun showError(context: Context, @StringRes messageResource: Int) {
        toastMessage.value = context.getString(messageResource)
        isSuccess.value = false
        isToastVisible.value = true
    }

    fun showNotification(context: Context, @StringRes messageResource: Int, isSuccess: Boolean) {
        if(isSuccess){
            showSuccess(context, messageResource)
        }
        else {
            showError(context, messageResource)
        }
    }

    fun hideToast() {
        isToastVisible.value = false
    }
}
