package com.bmexcs.pickpic.presentation.viewmodels

import android.util.Log
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bmexcs.pickpic.data.models.User
import com.bmexcs.pickpic.data.repositories.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

private const val TAG = "ProfileViewModel"

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {

    // UI state for the profile (use StateFlow to observe profile data in the UI)
    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    private var emailInput by mutableStateOf("")
    private var phoneInput by mutableStateOf("")

    val isEmailValid by derivedStateOf {
        emailInput.isNotEmpty() &&
                android.util.Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()
    }

    val isPhoneValid by derivedStateOf {
        phoneInput.isEmpty() ||
                android.util.Patterns.PHONE.matcher(phoneInput).matches()
    }

    // Loads the current profile.
    fun loadProfile() {
        viewModelScope.launch {
            Log.i(TAG, "Loading profile")
            val result = userRepository.getUser()
            _user.value = result

            emailInput = result.email
            phoneInput = result.phone
        }
    }

    // Updates the display name field.
    fun updateDisplayName(displayName: String) {
        _user.update { currentProfile ->
            currentProfile?.copy(display_name = displayName)
        }
    }

    // Updates the email field.
    fun updateEmail(email: String) {
        emailInput = email
        _user.update { currentProfile ->
            currentProfile?.copy(email = email)
        }
    }

    // Updates the phone number field.
    fun updatePhoneNumber(phoneNumber: String) {
        phoneInput = phoneNumber
        _user.update { currentProfile ->
            currentProfile?.copy(phone = phoneNumber)
        }
    }

    // Submits the user profile update.
    fun submit() {
        viewModelScope.launch {
            _user.value?.let { userRepository.updateUser(it) }
        }
    }

    // Logs the user out.
    fun logOut() {
        viewModelScope.launch {
            userRepository.signOut()
        }
    }
}
