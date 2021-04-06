package com.example.smsapp.ui.home

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.smsapp.R
import com.example.smsapp.databinding.ActivityMainBinding
import com.example.smsapp.ui.adapter.SmsListAdapter
import com.example.smsapp.utils.LOCAL_SMS_NOTIFIER
import com.example.smsapp.utils.showToast
import com.example.smsapp.utils.viewBinding


class MainActivity : AppCompatActivity() {

    private val binding by viewBinding(ActivityMainBinding::inflate)

    private val adapter by lazy { SmsListAdapter() }

    private val smsViewModel: SmsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        getPermissions()
        initViews()
    }

    private fun initViews() {
        binding.rvSmsList.adapter = adapter
        smsViewModel.smsListLiveData.observe(this) {
            adapter.swapData(it)
        }
        smsViewModel.errorToastEvent.observe(this) {
            showToast("No Message to show")
        }
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
            smsViewModel.getAllSms()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_SMS_CODE && !checkPermissions()) {
            smsViewModel.getAllSms()
        } else {
            showToast(R.string.error_sms_permissions)
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        this.registerUnRegisterNetworkReceiver(true)
    }

    override fun onStop() {
        this.registerUnRegisterNetworkReceiver(false)
        super.onStop()
    }

    companion object {
        const val REQUEST_SMS_CODE = 1
    }

    private val smsReceiverNotifier = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent != null) {
                smsViewModel.getAllSms()
            }
        }
    }
}

