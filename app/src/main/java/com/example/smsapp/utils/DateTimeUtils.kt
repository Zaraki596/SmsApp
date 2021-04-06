package com.example.smsapp.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

@SuppressLint("SimpleDateFormat")
fun convertLongToTime(time: Long): String {
    val date = Date(time)
    val format = SimpleDateFormat("yyyy.MM.dd HH:mm")
    return format.format(date)
}

fun getRemainingTimeInHours(timeInMillis: Long): Long {
    val current = Calendar.getInstance().timeInMillis
    val difference = current - timeInMillis
    return TimeUnit.MILLISECONDS.toHours(difference)
}

fun checkTimeRangeLong(hours: Long): Long {
    return when (hours.toInt()) {
        0 -> {
            0
        }
        1 -> {
            1
        }
        2 -> {
            2
        }
        in 3..5 -> {
            3
        }
        in 6..11 -> {
            6
        }
        in 12..23 -> {
            12
        }
        24 -> {
            24
        }
        else -> {
            25
        }
    }
}

fun checkTimeRange(hours: Long): String {
    return when (hours.toInt()) {
        0 -> {
            "In the last hour"
        }
        1 -> {
            "1 hours ago"
        }
        2 -> {
            "2 hours ago"
        }
        in 3..5 -> {
            "3 hours ago"
        }
        in 6..11 -> {
            "6 hours ago"
        }
        in 12..23 -> {
            "12 hours ago"
        }
        24 -> {
            "1 day ago"
        }
        else -> {
            "Older"
        }
    }
}
