package com.bmexcs.pickpic.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bmexcs.pickpic.data.models.UserInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import com.bmexcs.pickpic.data.repositories.EventRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject



@HiltViewModel
class InviteViewModel @Inject constructor(private val eventRepository: EventRepository) : ViewModel() {
    private val _emailList = MutableStateFlow<List<String>>(emptyList())
    val emailList: StateFlow<List<String>> = _emailList

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _invitedUsers = MutableStateFlow<List<UserInfo>>(emptyList())
    val invitedUsers: StateFlow<List<UserInfo>> = _invitedUsers

    fun addEmail(email: String) {
        _emailList.value += email
    }

    fun removeEmail(email: String) {
        _emailList.value -= email
    }

    fun confirmInvites(emails: List<String>, eventId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // This would call a repository method to send invites
                // Example: eventRepository.inviteUsers(eventId, emails)
                // THis stuff isn't done so u can prob trash it in a merge

                // After successful invite, refresh the invited users list
                loadInvitedUsers(eventId)

                // Clear the email list after successful invite
                _emailList.value = emptyList()
            } catch (e: Exception) {
                _error.value = "Failed to send invites: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadInvitedUsers(eventId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Use the existing getUsers method from EventRepository
                val users = eventRepository.getEventUsers(eventId)
                Log.d("InviteViewModel", "Loaded invited users: $users")
                _invitedUsers.value = users
            } catch (e: Exception) {
                _error.value = "Failed to load invited users: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
