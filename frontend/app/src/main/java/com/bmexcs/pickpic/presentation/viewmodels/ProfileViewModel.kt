package com.bmexcs.pickpic.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bmexcs.pickpic.data.models.Profile
import com.bmexcs.pickpic.data.repositories.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository
) : ViewModel() {

    // UI state for the profile (use StateFlow to observe profile data in the UI)
    private val _profile = MutableStateFlow<Profile?>(null)
    val profile: StateFlow<Profile?> = _profile

    // Function to load the current profile
    fun loadProfile() {
        viewModelScope.launch {
            val result = profileRepository.getProfile()
            _profile.value = result // Update the UI state with the profile data
        }
    }

    // Function to save the profile
    fun saveProfile(displayName: String, email: String, phone: String) {
        val currentProfile = _profile.value
        if (currentProfile != null) {
            // Update the profile data
            val updatedProfile = currentProfile.copy(
                displayName = displayName,
                email = email,
                phone = phone
            )
            viewModelScope.launch {
                profileRepository.saveProfile(updatedProfile)
                _profile.value = updatedProfile // Update UI state after saving
            }
        }
    }

    // Optionally, log out function if you need
    fun logout() {
        // Handle the logout logic (e.g., clearing session, token, etc.)
    }
}
