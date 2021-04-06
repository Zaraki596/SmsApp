package com.example.smsapp.utils

import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

fun convertLongToTime(time: Long): String {
    val date = Date(time)
    val format = SimpleDateFormat("yyyy.MM.dd HH:mm")
    return format.format(date)
}

fun currentTimeToLong(): Long {
    return System.currentTimeMillis()
}

fun getRemainingTimeInHours(timeInMillis: Long): Long {
    val current = Calendar.getInstance().timeInMillis
    val difference = current - timeInMillis
    return TimeUnit.MILLISECONDS.toHours(difference)
}

fun checkTimeRange(hours: Long) {
    when (hours.toInt()) {
        0 -> {
        }
        1 -> {
        }
        2 -> {
        }
        in 3..5 -> {
        }
        in 6..11 -> {
        }
        in 12..23 -> {
        }
        24 -> {
        }
        else -> {
        }
    }
}