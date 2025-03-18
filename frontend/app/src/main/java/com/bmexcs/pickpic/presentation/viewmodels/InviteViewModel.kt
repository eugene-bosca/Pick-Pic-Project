package com.bmexcs.pickpic.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bmexcs.pickpic.data.models.InvitedUser
import com.bmexcs.pickpic.data.models.UserInfo
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
    private val _emailList = MutableStateFlow<List<String>>(emptyList())
    val emailList: StateFlow<List<String>> = _emailList

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage
    // Alias for UI usage (matches viewModel.error in the UI)
    val error: StateFlow<String?> = _errorMessage

    // New state for invited users
    private val _invitedUsers = MutableStateFlow<List<InvitedUser>>(emptyList())
    val invitedUsers: StateFlow<List<InvitedUser>> = _invitedUsers

    fun addEmail(email: String) {
        _emailList.value += email
    }

    fun removeEmail(email: String) {
        _emailList.value -= email
    }

    /**
     * Loads the invited users for the given eventId.
     * Assumes that eventRepository provides a method getInvitedUsers(eventId: String): List<UserInfo>.
     */
    fun loadInvitedUsers(eventId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val users = withContext(Dispatchers.IO) {
                    eventRepository.getEventUsers(eventId)
                }
                _invitedUsers.value = users
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
                    eventRepository.inviteUsersWithId(userIds, eventId)
                }
                _errorMessage.value = null
                // Clear the email list after successful invitation
                _emailList.value = emptyList()
                // Refresh the invited users list
                loadInvitedUsers(eventId)
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
                loadInvitedUsers(eventId)
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage ?: "An unknown error occurred"
                Log.e("EventInvitationViewModel", "Error declining event", e)
            }
        }
    }
    fun isCurrentUserOwner(ownerId: String): Boolean {
        return userRepository.getUser().user_id == ownerId
    }

}
