package com.bmexcs.pickpic.presentation.viewmodels

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
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

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {

    // UI state for the profile (use StateFlow to observe profile data in the UI)
    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    // TODO: use the validators
    // https://developer.android.com/develop/ui/compose/quick-guides/content/validate-input
    val isEmailValid by derivedStateOf {
        val email = _user.value?.email.orEmpty()
        email.isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    val isPhoneValid by derivedStateOf {
        val phone = _user.value?.phone.orEmpty()
        phone.isNotEmpty() && android.util.Patterns.PHONE.matcher(phone).matches()
    }

    // Loads the current profile.
    fun loadProfile() {
        viewModelScope.launch {
            val result = userRepository.getUser()
            if (result != null) {
                _user.value = result // Cache in ViewModel
            }
        }
    }

    // Updates the display name field.
    fun updateDisplayName(displayName: String) {
        _user.update { currentProfile ->
            currentProfile?.copy(display_name = displayName)
        }

        viewModelScope.launch {
            _user.value?.let { userRepository.updateUser(it) }
        }
    }

    // Updates the email field.
    fun updateEmail(email: String) {
        _user.update { currentProfile ->
            currentProfile?.copy(email = email)
        }

        viewModelScope.launch {
            _user.value?.let { userRepository.updateUser(it) }
        }
    }

    // Updates the phone number field.
    fun updatePhoneNumber(phoneNumber: String) {
        _user.update { currentProfile ->
            currentProfile?.copy(phone = phoneNumber)
        }

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
