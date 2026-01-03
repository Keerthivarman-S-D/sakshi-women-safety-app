package com.example.sakshi.contacts


import androidx.lifecycle.ViewModel
import com.example.sakshi.model.EmergencyContact
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ContactsViewModel : ViewModel() {

    private val repo = ContactsRepository()

    private val _contacts = MutableStateFlow<List<EmergencyContact>>(emptyList())
    val contacts: StateFlow<List<EmergencyContact>> = _contacts

    fun load(uid: String) {
        repo.loadContacts(uid) {
            _contacts.value = it
        }
    }

    fun add(uid: String, contact: EmergencyContact) {
        val updated = _contacts.value + contact
        _contacts.value = updated
        repo.saveContacts(uid, updated)
    }

    fun remove(uid: String, contact: EmergencyContact) {
        val updated = _contacts.value - contact
        _contacts.value = updated
        repo.saveContacts(uid, updated)
    }
}
