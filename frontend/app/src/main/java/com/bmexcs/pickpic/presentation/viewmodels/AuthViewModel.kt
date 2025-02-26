package com.bmexcs.pickpic.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bmexcs.pickpic.data.repositories.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    val authState = authRepository.authState

    fun signInWithGoogle(onSuccess: () -> Unit, onFailure: () -> Unit) {
        viewModelScope.launch {
            val success = authRepository.signInWithGoogle()
            if (success) onSuccess() else onFailure()
        }
    }

    fun signOut() {
        authRepository.signOut()
    }
}
