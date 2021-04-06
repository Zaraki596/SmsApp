package com.example.smsapp.reciever

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.telephony.SmsMessage
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.smsapp.R
import com.example.smsapp.ui.MainActivity
import com.example.smsapp.utils.LOCAL_SMS_NOTIFIER
import com.example.smsapp.utils.NOTIFICATION_CHANNEL_ID_PRIMARY

class SmsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == SMS_RECEIVED) {
            val bundle = intent.extras
            if (bundle != null) {
                // get sms objects
                val pdus = bundle["pdus"] as Array<Any>?
                if (pdus!!.isEmpty()) {
                    return
                }
                // large message might be broken into many
                val messages = arrayOfNulls<SmsMessage>(
                    pdus.size
                )
                val sb = StringBuilder()
                for (i in pdus.indices) {
                    messages[i] = SmsMessage.createFromPdu(pdus[i] as ByteArray)
                    sb.append(messages[i]?.messageBody)
                }
                val sender = messages[0]!!.originatingAddress ?: "No sender Found"
                val message = sb.toString()
                showNotification(context, sender, message)
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                // prevent any other broadcast receivers from receiving broadcast
                // abortBroadcast();
            }
        }
    }

    @SuppressLint("StringFormatInvalid")
    private fun showNotification(
        context: Context,
        title: String,
        message: String
    ) {
        val intent = Intent(context, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_ONE_SHOT
        )

        val channelId = NOTIFICATION_CHANNEL_ID_PRIMARY
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setColor(ContextCompat.getColor(context, R.color.purple_200))
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager =
            NotificationManagerCompat.from(context)


        notificationManager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
        LocalBroadcastManager.getInstance(context).sendBroadcast(Intent(LOCAL_SMS_NOTIFIER))
    }

    companion object {
        private const val SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED"
    }
}