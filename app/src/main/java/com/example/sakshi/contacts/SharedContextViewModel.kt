package com.example.sakshi.contacts
import androidx.lifecycle.ViewModel
import com.example.sakshi.model.EmergencyContact
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SharedContactsViewModel(
    private val uid: String
) : ViewModel() {

    private val repo = ContactsRepository()

    private val _contacts =
        MutableStateFlow<List<EmergencyContact>>(emptyList())
    val contacts: StateFlow<List<EmergencyContact>> = _contacts

    fun loadContacts() {
        repo.loadContacts(uid) {
            _contacts.value = it
        }
    }

    fun addContact(contact: EmergencyContact) {
        val updated = _contacts.value + contact
        _contacts.value = updated
        repo.saveContacts(uid, updated)
    }

    fun removeContact(contact: EmergencyContact) {
        val updated = _contacts.value - contact
        _contacts.value = updated
        repo.saveContacts(uid, updated)
    }
}
