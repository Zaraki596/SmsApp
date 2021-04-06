package com.example.smsapp.ui.home

import SingleLiveEvent
import android.app.Application
import android.net.Uri
import android.provider.Telephony
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.smsapp.SmsApp
import com.example.smsapp.data.model.HoursItem
import com.example.smsapp.data.model.SmsItem
import com.example.smsapp.data.model.ViewItem
import com.example.smsapp.utils.checkTimeRangeLong
import com.example.smsapp.utils.convertLongToTime
import com.example.smsapp.utils.getRemainingTimeInHours
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SmsViewModel(application: Application) : AndroidViewModel(application) {

    private val _smsListLiveData = MutableLiveData<List<ViewItem>>()
    val smsListLiveData: LiveData<List<ViewItem>> get() = _smsListLiveData

    val errorToastEvent = SingleLiveEvent<Unit>()

    fun getAllSms() {
        viewModelScope.launch(Dispatchers.IO) {
            this@SmsViewModel.loadSms()
        }
    }

    private fun loadSms(){
        val smsListItem = mutableListOf<ViewItem>()
        val contentResolver = getApplication<SmsApp>().contentResolver
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
                    val passedHours = checkTimeRangeLong(hours)

                    val smsItem = SmsItem(
                        smsDate = smsDate.toLong(),
                        body = body,
                        number = number,
                        formattedDate = dateFormat,
                        hours = hours
                    )

                    if (lastHour != passedHours) {
                        lastHour = passedHours
                        val hoursItem = HoursItem(hoursPassed = passedHours)
                        smsListItem.add(ViewItem.DateItem(hoursItem))
                    }
                    smsListItem.add(ViewItem.SMSItem(smsItem))
                    cursor.moveToNext()
                }
            }
            cursor.close()
            _smsListLiveData.postValue(smsListItem)
        } else {
            errorToastEvent.postValue(Unit)
        }
    }
}