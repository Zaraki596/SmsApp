package com.example.smsapp.ui

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Telephony
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.smsapp.data.model.HoursItem
import com.example.smsapp.data.model.SmsItem
import com.example.smsapp.data.model.ViewItem
import com.example.smsapp.databinding.ActivityMainBinding
import com.example.smsapp.ui.adapter.SmsListAdapter
import com.example.smsapp.utils.LOCAL_SMS_NOTIFIER
import com.example.smsapp.utils.convertLongToTime
import com.example.smsapp.utils.getRemainingTimeInHours
import com.example.smsapp.utils.viewBinding


class MainActivity : AppCompatActivity() {

    private val binding by viewBinding(ActivityMainBinding::inflate)

    private val adapter by lazy { SmsListAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        getPermissions()
    }


    override fun onStart() {
        super.onStart()
        this.registerUnRegisterNetworkReceiver(true)
    }

    override fun onStop() {
        this.registerUnRegisterNetworkReceiver(false)
        super.onStop()
    }

    private fun registerUnRegisterNetworkReceiver(register: Boolean) {
        LocalBroadcastManager.getInstance(this).let { localBroadcastManager ->
            if (register) {
                localBroadcastManager.registerReceiver(
                    smsReceiverNotifier,
                    IntentFilter(LOCAL_SMS_NOTIFIER)
                )
            } else {
                localBroadcastManager.unregisterReceiver(smsReceiverNotifier)
            }
        }

    }

    private fun checkPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.READ_SMS
        ) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.RECEIVE_SMS
        ) != PackageManager.PERMISSION_GRANTED
    }

    private fun getPermissions() {
        if (checkPermissions()) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS),
                REQUEST_SMS_CODE
            )
        } else {
            getAllSms()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_SMS_CODE && !checkPermissions()) {
            getAllSms()
        } else {
            Toast.makeText(this, "Please allow all permissions", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    fun getAllSms() {
        val smsListItem = mutableListOf<ViewItem>()
        val contentResolver = this.contentResolver
        val uri = Uri.parse("content://sms/inbox")
        val cursor = contentResolver.query(uri, null, null, null, null)
        var totalSms = 0
        var lastHour: Long = -1
        if (cursor != null) {
            totalSms = cursor.count
            if (cursor.moveToFirst()) {

                for (sms in 1..totalSms) {
                    val smsDate = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.DATE))
                    val number =
                        cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.ADDRESS))
                    val body = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.BODY))
                    val dateFormat = convertLongToTime(smsDate.toLong())
                    val hours = getRemainingTimeInHours(smsDate.toLong())

                    val smsItem = SmsItem(
                        smsDate = smsDate.toLong(),
                        body = body,
                        number = number,
                        formattedDate = dateFormat,
                        hours = hours
                    )

                    if (lastHour != hours) {
                        lastHour = hours
                        val hoursItem = HoursItem(hoursPassed = hours)
                        smsListItem.add(ViewItem.DateItem(hoursItem))
                    }
                    smsListItem.add(ViewItem.SMSItem(smsItem))
                    cursor.moveToNext()
                }
            }
            cursor.close()
            binding.rvSmsList.adapter = adapter
            adapter.swapData(smsListItem)
        } else {
            Toast.makeText(this, "No message to Show", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        const val REQUEST_SMS_CODE = 1
        private const val MY_PERMISSIONS_REQUEST_SMS_RECEIVE = 10
    }

    private val smsReceiverNotifier = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent != null) {
                this@MainActivity.getAllSms()
            }
        }
    }
}

