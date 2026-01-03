package com.example.sakshi.sos

import com.google.firebase.database.*

class LiveSosRepository {

    private val db =
        FirebaseDatabase.getInstance().getReference("sos_events")

    fun listenToSos(
        uid: String,
        onUpdate: (Double, Double) -> Unit
    ) {
        db.child(uid).addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val lat =
                        snapshot.child("latitude")
                            .getValue(Double::class.java)
                    val lon =
                        snapshot.child("longitude")
                            .getValue(Double::class.java)

                    if (lat != null && lon != null) {
                        onUpdate(lat, lon)
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            }
        )
    }
}
