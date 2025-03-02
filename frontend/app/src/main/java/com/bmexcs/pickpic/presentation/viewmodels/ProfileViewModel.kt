package com.bmexcs.pickpic.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bmexcs.pickpic.data.models.Profile
import com.bmexcs.pickpic.data.repositories.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
) : ViewModel() {

    // UI state for the profile (use StateFlow to observe profile data in the UI)
    private val _profile = MutableStateFlow<Profile?>(null)
    val profile: StateFlow<Profile?> = _profile

    // Loads the current profile.
    fun loadProfile() {
        viewModelScope.launch {
            val result = profileRepository.getProfile()
            if (result != null) {
                _profile.value = result // Cache in ViewModel
            }
        }
    }

    // Updates the display name field.
    fun updateDisplayName(displayName: String) {
        _profile.update { currentProfile ->
            currentProfile?.copy(displayName = displayName)
        }

        viewModelScope.launch {
            _profile.value?.let { profileRepository.saveProfile(it) }
        }
    }

    // Updates the email field.
    fun updateEmail(email: String) {
        _profile.update { currentProfile ->
            currentProfile?.copy(email = email)
        }

        viewModelScope.launch {
            _profile.value?.let { profileRepository.saveProfile(it) }
        }
    }

    // Updates the phone number field.
    fun updatePhoneNumber(phoneNumber: String) {
        _profile.update { currentProfile ->
            currentProfile?.copy(phone = phoneNumber)
        }

        viewModelScope.launch {
            _profile.value?.let { profileRepository.saveProfile(it) }
        }
    }

    // Logs the user out.
    fun logout() {
        // Handle the logout logic (e.g., clearing session, token, etc.)
    }
}
