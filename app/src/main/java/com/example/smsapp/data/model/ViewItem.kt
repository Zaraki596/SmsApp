package com.example.smsapp.data.model

import com.example.smsapp.R

sealed class ViewItem(val resource: Int) {
    class DateItem(val hoursItem: HoursItem) :
        ViewItem(R.layout.item_date_header)

    class SMSItem(val smsItem: SmsItem) : ViewItem(R.layout.item_sms)
}
