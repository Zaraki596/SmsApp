package com.example.smsapp.data.model

data class SmsItem(
    val number: String,
    val smsDate: Long,
    val body: String,
    val formattedDate: String,
    val hours: Long
)
