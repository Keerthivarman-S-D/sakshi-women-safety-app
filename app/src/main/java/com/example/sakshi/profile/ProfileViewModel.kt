package com.example.sakshi.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sakshi.model.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val repo: ProfileRepository = ProfileRepository()
) : ViewModel() {

    private val _profile = MutableStateFlow(UserProfile())
    val profile: StateFlow<UserProfile> = _profile

    fun load(uid: String) {
        viewModelScope.launch {
            repo.getProfile(uid)?.let {
                _profile.value = it
            }
        }
    }

    fun save(uid: String) {
        viewModelScope.launch {
            repo.saveProfile(uid, _profile.value)
        }
    }

    fun update(profile: UserProfile) {
        _profile.value = profile
    }
}
