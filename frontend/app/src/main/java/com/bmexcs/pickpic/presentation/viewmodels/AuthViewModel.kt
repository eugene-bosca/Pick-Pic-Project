package com.bmexcs.pickpic.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bmexcs.pickpic.data.utils.SignInResult
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

    private fun checkSignedInUser() {
        _signInResult.value = try {
            authRepository.getCurrentUser()
            SignInResult.Success
        } catch (e: Exception) {
            SignInResult.UnknownError
        }
    }
}
