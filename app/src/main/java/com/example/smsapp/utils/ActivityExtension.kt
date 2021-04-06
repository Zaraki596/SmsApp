package com.example.smsapp.utils

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes

fun Context.showToast(message: String, length: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, length).show()
}

fun Context.showToast(@StringRes msgRes: Int, length: Int = Toast.LENGTH_SHORT) {
    showToast(getString(msgRes),length)
}