package com.example.tasktrackr_app.utils

import android.content.Context
import com.example.tasktrackr_app.R
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object SessionManager {

    private val _sessionEvents = MutableSharedFlow<SessionEvent>(replay = 0)
    val sessionEvents = _sessionEvents.asSharedFlow()

    sealed class SessionEvent {
        data class SessionExpired(val message: String = "Session expired") : SessionEvent()
    }

    suspend fun notifySessionExpired(context: Context, message: String = "Session expired") {
        _sessionEvents.emit(SessionEvent.SessionExpired(message))
        NotificationHelper.showNotification(context, R.string.session_expired, false)
    }


}
