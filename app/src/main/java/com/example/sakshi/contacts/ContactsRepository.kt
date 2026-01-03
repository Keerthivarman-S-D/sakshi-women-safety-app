package com.example.sakshi.contacts

import com.example.sakshi.model.EmergencyContact
import com.google.firebase.firestore.FirebaseFirestore

class ContactsRepository {

    private val db = FirebaseFirestore.getInstance()

    fun saveContacts(uid: String, contacts: List<EmergencyContact>) {
        db.collection("users")
            .document(uid)
            .set(mapOf("emergencyContacts" to contacts))
    }

    fun loadContacts(
        uid: String,
        onResult: (List<EmergencyContact>) -> Unit
    ) {
        db.collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { doc ->
                val list = doc.toObject(UserContacts::class.java)
                    ?.emergencyContacts ?: emptyList()
                onResult(list)
            }
    }

    private data class UserContacts(
        val emergencyContacts: List<EmergencyContact> = emptyList()
    )
}
