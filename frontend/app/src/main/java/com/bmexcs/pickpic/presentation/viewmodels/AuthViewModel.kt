package com.bmexcs.pickpic.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bmexcs.pickpic.data.services.SignInResult
import com.bmexcs.pickpic.data.repositories.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "AuthViewModel"

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
        viewModelScope.launch {
            _signInResult.value = try {
                authRepository.getCurrentUser()
                authRepository.initUserState()
                SignInResult.Success
            } catch (e: Exception) {
                Log.e(TAG, "checkSignedInUser error: $e")
                null
            }
        }
    }
}
