package com.example.sakshi.profile

import com.example.sakshi.model.UserProfile
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ProfileRepository {

    private val db = FirebaseFirestore.getInstance()

    suspend fun getProfile(uid: String): UserProfile? {
        val doc = db.collection("users").document(uid).get().await()
        return doc.toObject(UserProfile::class.java)
    }

    suspend fun saveProfile(uid: String, profile: UserProfile) {
        db.collection("users").document(uid).set(profile)
    }
}
