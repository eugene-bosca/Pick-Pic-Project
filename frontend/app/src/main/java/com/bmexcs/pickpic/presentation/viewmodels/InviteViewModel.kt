package com.bmexcs.pickpic.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bmexcs.pickpic.data.models.Email
import com.bmexcs.pickpic.data.models.UserId
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

    fun addEmail(email: String) {
        _emailList.value += email
    }

    fun removeEmail(email: String) {
        _emailList.value -= email
    }

    fun confirmInvites(emailList: List<String>) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Loop through each email to fetch the userId one by one on the IO dispatcher.
                val userIds = withContext(Dispatchers.IO) {
                    val ids = mutableListOf<UserId>()
                    for (email in emailList) {
                        // Replace this with your actual call that retrieves a UserId for a single email.
                        val userId = userRepository.getUserIdWithEmail(email)
                        ids.add(userId)
                    }
                    ids
                }
                // Get event id from repository (assuming it has been set)
                val eventId = eventRepository.event.value.event_id

                // Send invite request on the IO dispatcher
                withContext(Dispatchers.IO) {
                    eventRepository.inviteUsersWithId(userIds, eventId)
                }
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage ?: "An unknown error occurred"
                Log.e("InviteViewModel", "Error inviting users", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}
