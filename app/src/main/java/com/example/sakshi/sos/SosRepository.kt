package com.example.sakshi.sos

import com.google.firebase.database.FirebaseDatabase

class SosRepository {

    private val db =
        FirebaseDatabase.getInstance().getReference("sos_events")

    fun createEvent(
        uid: String,
        lat: Double,
        lon: Double
    ) {
        val data = mapOf(
            "latitude" to lat,
            "longitude" to lon,
            "timestamp" to System.currentTimeMillis(),
            "status" to "active"
        )

        db.child(uid).setValue(data)
    }

    fun updateLocation(
        uid: String,
        lat: Double,
        lon: Double
    ) {
        val data = mapOf(
            "latitude" to lat,
            "longitude" to lon,
            "timestamp" to System.currentTimeMillis(),
            "status" to "active"
        )

        db.child(uid).setValue(data)
    }

    fun stopSos(uid: String) {
        db.child(uid).child("status").setValue("resolved")
    }
}
