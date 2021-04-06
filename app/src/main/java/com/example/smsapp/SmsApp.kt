package com.example.smsapp

import android.annotation.SuppressLint
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import com.example.smsapp.utils.*

class SmsApp: Application() {


    override fun onCreate() {
        super.onCreate()
        this.noCreateNotificationChannel()
    }

    @SuppressLint("WrongConstant")
    fun noCreateNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager=getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            val mChannel =
                NotificationChannel(
                    NOTIFICATION_CHANNEL_ID_PRIMARY,
                    NOTIFICATION_CHANNEL_NAME_PRIMARY,
                    NotificationManager.IMPORTANCE_HIGH
                )
            notificationManager.run {
                this.getNotificationChannel(NOTIFICATION_CHANNEL_ID_PRIMARY)
                    ?: this.createNotificationChannel(
                        mChannel
                    )
            }

            val sChannel =
                NotificationChannel(
                    NOTIFICATION_CHANNEL_ID_NORMAL,
                    NOTIFICATION_CHANNEL_NAME_NORMAL,
                    NotificationManager.IMPORTANCE_DEFAULT
                )
            sChannel.vibrationPattern = longArrayOf()
            sChannel.setSound(null, null)
            notificationManager.run {
                this.getNotificationChannel(NOTIFICATION_CHANNEL_ID_NORMAL)
                    ?: this.createNotificationChannel(
                        sChannel
                    )
            }

            val pChannel =
                NotificationChannel(
                    NOTIFICATION_CHANNEL_ID_NORMAL_HIGH,
                    NOTIFICATION_CHANNEL_NAME_NORMAL_HIGH,
                    NotificationManager.IMPORTANCE_HIGH
                )
            pChannel.vibrationPattern = longArrayOf()
            pChannel.setSound(null, null)
            notificationManager.run {
                this.getNotificationChannel(NOTIFICATION_CHANNEL_ID_NORMAL_HIGH)
                    ?: this.createNotificationChannel(
                        pChannel
                    )
            }
        }
    }
}