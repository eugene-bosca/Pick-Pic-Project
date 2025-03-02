package com.bmexcs.pickpic.presentation.viewmodels

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
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

    // TODO: use the validators
    // https://developer.android.com/develop/ui/compose/quick-guides/content/validate-input
    val isEmailValid by derivedStateOf {
        val email = _profile.value?.email.orEmpty()
        email.isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    val isPhoneValid by derivedStateOf {
        val phone = _profile.value?.phone.orEmpty()
        phone.isNotEmpty() && android.util.Patterns.PHONE.matcher(phone).matches()
    }

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
    fun logOut() {
        viewModelScope.launch {
            profileRepository.signOut()
        }
    }
}
