package com.example.sakshi.contacts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class SharedContactsViewModelFactory(
    private val uid: String
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SharedContactsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SharedContactsViewModel(uid) as T
        }
        throw IllegalArgumentException("Unknown ViewModel")
    }
}
