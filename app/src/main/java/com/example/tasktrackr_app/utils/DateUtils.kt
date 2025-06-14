package com.example.tasktrackr_app.utils

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

object DateUtils {
    const val DISPLAY_DATE_PATTERN = "dd/MM/yyyy"
    const val FULL_DATE_PATTERN = "HH:mm dd/MM/yyyy"
    const val API_DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"

    private val justDateFormatter: SimpleDateFormat
        get() = SimpleDateFormat(DISPLAY_DATE_PATTERN, Locale.getDefault()).apply {
            timeZone = TimeZone.getDefault()
        }

    private val justTimeFormatter: SimpleDateFormat
        get() = SimpleDateFormat("HH:mm", Locale.getDefault()).apply {
            timeZone = TimeZone.getDefault()
        }

    private val displayDateFormatter: SimpleDateFormat
        get() = SimpleDateFormat(DISPLAY_DATE_PATTERN, Locale.getDefault()).apply {
            timeZone = TimeZone.getDefault()
        }

    private val fullDateFormatter: SimpleDateFormat
        get() = SimpleDateFormat(FULL_DATE_PATTERN, Locale.getDefault()).apply {
            timeZone = TimeZone.getDefault()
        }

    private val apiDateFormatter: SimpleDateFormat
        get() = SimpleDateFormat(API_DATE_PATTERN, Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }

    fun formatFullDate(date: Date?): String {
        return date?.let { fullDateFormatter.format(it) } ?: "N/A"
    }

    fun formatDisplayDate(date: Date?): String {
        return date?.let { displayDateFormatter.format(it) } ?: "N/A"
    }

    fun formatDateOnly(date: Date?): String {
        return date?.let { justDateFormatter.format(it) } ?: ""
    }

    fun formatTime(date: Date?): String {
        return date?.let { justTimeFormatter.format(it) } ?: ""
    }

    fun formatApiDate(date: Date?): String? {
        return date?.let { apiDateFormatter.format(it) }
    }

    fun parseApiDate(dateString: String?): Date? {
        if (dateString.isNullOrBlank()) return null
        return try {
            apiDateFormatter.parse(dateString)
        } catch (e: ParseException) {
            parseStringToDate(dateString, "yyyy-MM-dd'T'HH:mm:ss.SSSX")
                ?: parseStringToDate(dateString, "yyyy-MM-dd'T'HH:mm:ss")
                ?: parseStringToDate(dateString, "yyyy-MM-dd")
        }
    }

    fun parseStringToDate(dateString: String?, pattern: String = FULL_DATE_PATTERN): Date? {
        if (dateString.isNullOrBlank()) return null
        return try {
            SimpleDateFormat(pattern, Locale.getDefault()).parse(dateString)
        } catch (e: ParseException) {
            e.printStackTrace()
            null
        }
    }

    fun createDate(year: Int, month: Int, dayOfMonth: Int, hour: Int = 0, minute: Int = 0): Date {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, dayOfMonth)
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.time
    }

    fun combineDateAndTime(newDate: Date, existingDate: Date?): Date {
        val calendar = Calendar.getInstance()
        calendar.time = newDate

        val existingCalendar = Calendar.getInstance()
        if (existingDate != null) {
            existingCalendar.time = existingDate
        } else {
            existingCalendar.time = Date()
        }

        calendar.set(Calendar.HOUR_OF_DAY, existingCalendar.get(Calendar.HOUR_OF_DAY))
        calendar.set(Calendar.MINUTE, existingCalendar.get(Calendar.MINUTE))
        calendar.set(Calendar.SECOND, existingCalendar.get(Calendar.SECOND))
        calendar.set(Calendar.MILLISECOND, existingCalendar.get(Calendar.MILLISECOND))

        return calendar.time
    }

    fun updateTime(date: Date, hour: Int, minute: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.time
    }

    fun calculateTimeSpent(startDate: Date?, endDate: Date?): String {
        if (startDate == null || endDate == null) return "N/A"

        val actualEndDate = if (endDate.after(Date())) {
            Date()
        } else {
            endDate
        }

        val diffMillis = actualEndDate.time - startDate.time
        val hours = TimeUnit.MILLISECONDS.toHours(diffMillis)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(diffMillis) % 60

        return when {
            hours > 0 -> "${hours}h ${minutes}m"
            minutes > 0 -> "${minutes}m"
            else -> "0m"
        }
    }

    fun calculateTimeSpentInMinutes(startDate: Date?, endDate: Date?): Long {
        if (startDate == null || endDate == null) return 0L

        val actualEndDate = if (endDate.after(Date())) {
            Date()
        } else {
            endDate
        }

        val diffMillis = actualEndDate.time - startDate.time
        return TimeUnit.MILLISECONDS.toMinutes(diffMillis).coerceAtLeast(0L)
    }

    fun formatMinutesToHm(minutes: Long): String {
        if (minutes <= 0) return "0m"

        val hours = minutes / 60
        val remainingMinutes = minutes % 60

        return when {
            hours > 0 && remainingMinutes > 0 -> "${hours}h ${remainingMinutes}m"
            hours > 0 -> "${hours}h"
            else -> "${remainingMinutes}m"
        }
    }
}
