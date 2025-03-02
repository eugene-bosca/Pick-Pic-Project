package com.bmexcs.pickpic.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bmexcs.pickpic.data.models.SignInResult
import com.bmexcs.pickpic.data.repositories.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _signInResult = MutableStateFlow<SignInResult?>(null)
    val signInResult: StateFlow<SignInResult?> = _signInResult

    init {
        checkSignedInUser()
    }

    fun signInWithGoogle() {
        viewModelScope.launch {
            val result: SignInResult = authRepository.signInWithGoogle()
            _signInResult.value = result
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
        }
    }

    private fun checkSignedInUser() {
        _signInResult.value = if (authRepository.getCurrentUser() != null) {
            SignInResult.Success
        } else {
            SignInResult.NoCredentials
        }
    }

}
