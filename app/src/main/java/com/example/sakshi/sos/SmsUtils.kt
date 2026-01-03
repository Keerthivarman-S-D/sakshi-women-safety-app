package com.example.sakshi.sos

import android.telephony.SmsManager

fun sendSosSms(contacts: List<String>, lat: Double, lon: Double) {
    val message = "Help! My location is: https://www.google.com/maps/search/?api=1&query=$lat,$lon"
    val smsManager = SmsManager.getDefault()
    contacts.forEach { phone ->
        smsManager.sendTextMessage(phone, null, message, null, null)
    }
}
