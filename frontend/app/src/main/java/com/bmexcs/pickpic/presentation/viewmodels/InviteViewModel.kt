package com.bmexcs.pickpic.presentation.viewmodels

import android.util.Log
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bmexcs.pickpic.data.models.UserMetadata
import com.bmexcs.pickpic.data.repositories.EventRepository
import com.bmexcs.pickpic.data.repositories.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class InviteViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val eventRepository: EventRepository
) : ViewModel() {

    // Email input
    var emailInput by mutableStateOf("")

    val isEmailValid by derivedStateOf {
        emailInput.isEmpty() ||
                android.util.Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()
    }

    // List of invitee emails
    private val _emailList = MutableStateFlow<List<String>>(emptyList())
    val emailList: StateFlow<List<String>> = _emailList

    // New state for accepted users
    private val _acceptedUsers = MutableStateFlow<List<UserMetadata>>(emptyList())
    val acceptedUsers: StateFlow<List<UserMetadata>> = _acceptedUsers

    // New state for pending users
    private val _pendingUsers = MutableStateFlow<List<UserMetadata>>(emptyList())
    val pendingUsers: StateFlow<List<UserMetadata>> = _pendingUsers

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _errorMessage

    fun addEmail(email: String) {
        _emailList.value += email
    }

    fun removeEmail(email: String) {
        _emailList.value -= email
    }

    /**
     * Loads the accepted users for the given eventId.
     */
    fun loadAcceptedUsers(eventId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val users = withContext(Dispatchers.IO) {
                    eventRepository.getAcceptedUsersMetadata(eventId)
                }
                _acceptedUsers.value = users
            } catch(e: Exception) {
                _errorMessage.value = e.localizedMessage ?: "An unknown error occurred"
                Log.e("InviteViewModel", "Error loading invited users", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Loads the pending users for the given eventId.
     */
    fun loadPendingUsers(eventId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val users = withContext(Dispatchers.IO) {
                    eventRepository.getPendingUsersMetadata(eventId)
                }
                _pendingUsers.value = users
            } catch(e: Exception) {
                _errorMessage.value = e.localizedMessage ?: "An unknown error occurred"
                Log.e("InviteViewModel", "Error loading invited users", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Confirms the invites for the provided email list and eventId.
     * Upon successful invitation, it clears the email list and refreshes the invited users.
     */
    fun confirmInvites(emailList: List<String>, eventId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val userIds = withContext(Dispatchers.IO) {
                    userRepository.getUsersFromEmails(emailList)
                }
                withContext(Dispatchers.IO) {
                    eventRepository.inviteUsersFromEmail(userIds, eventId)
                }
                _errorMessage.value = null
                // Clear the email list after successful invitation
                _emailList.value = emptyList()
                // Refresh the invited users list
                loadAcceptedUsers(eventId)
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage ?: "An unknown error occurred"
                Log.e("InviteViewModel", "Error inviting users", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun kickUser(eventId: String, userId: String) {
        viewModelScope.launch {
            try {
                eventRepository.removeUserFromEvent(eventId, userId)
                loadAcceptedUsers(eventId)
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage ?: "An unknown error occurred"
                Log.e("EventInvitationViewModel", "Error declining event", e)
            }
        }
    }

    fun isCurrentUserOwner(ownerId: String): Boolean {
        return userRepository.getUser().id == ownerId
    }
}
