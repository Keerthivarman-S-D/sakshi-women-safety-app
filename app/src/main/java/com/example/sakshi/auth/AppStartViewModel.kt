package com.example.sakshi.auth

import kotlinx.coroutines.tasks.await
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

enum class StartDestination {
    OTP, CONTACTS, PERMISSIONS, HOME
}

class AppStartViewModel : ViewModel() {

    private val auth = AuthRepository()
    private val db = FirebaseFirestore.getInstance()

    private val _start = MutableStateFlow(StartDestination.OTP)
    val start: StateFlow<StartDestination> = _start

    fun decideStart() {
        if (!auth.isLoggedIn()) {
            _start.value = StartDestination.OTP
            return
        }

        val uid = auth.uid()

        viewModelScope.launch {
            val doc = db.collection("users").document(uid).get().await()
            val contacts = doc.get("emergencyContacts") as? List<*>

            _start.value =
                if (contacts.isNullOrEmpty())
                    StartDestination.CONTACTS
                else
                    StartDestination.HOME
        }
    }
}
