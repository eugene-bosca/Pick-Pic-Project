package com.bmexcs.pickpic.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bmexcs.pickpic.data.models.Profile
import com.bmexcs.pickpic.data.repositories.ProfileRepository
import com.bmexcs.pickpic.data.serializable.SerializableUUID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository
) : ViewModel() {

    // UI state for the profile (use StateFlow to observe profile data in the UI)
    private val _profile = MutableStateFlow<Profile?>(null)
    val profile: StateFlow<Profile?> = _profile

    // Loads the current profile.
    fun loadProfile() {
        viewModelScope.launch {
            val result = profileRepository.getProfile()
            _profile.value = result // Update the UI state with the profile data
        }
    }

    // Updates the display name field.
    fun updateDisplayName(displayName: String) {
        _profile.update { currentProfile ->
            currentProfile?.copy(displayName = displayName)
        }
    }

    // Updates the email field.
    fun updateEmail(email: String) {
        _profile.update { currentProfile ->
            currentProfile?.copy(email = email)
        }
    }

    // Updates the phone number field.
    fun updatePhoneNumber(phoneNumber: String) {
        _profile.update { currentProfile ->
            currentProfile?.copy(phone = phoneNumber)
        }
    }

    // Logs the user out.
    fun logout() {
        // Handle the logout logic (e.g., clearing session, token, etc.)
    }
}
