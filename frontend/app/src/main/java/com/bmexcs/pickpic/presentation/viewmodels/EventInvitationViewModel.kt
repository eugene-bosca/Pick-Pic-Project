package com.bmexcs.pickpic.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bmexcs.pickpic.data.models.EventInfo
import com.bmexcs.pickpic.data.models.EventMember
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
class EventInvitationViewModel @Inject constructor(
    private val eventRepository: EventRepository,
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _events = MutableStateFlow<List<EventInfo>>(emptyList())
    val events: StateFlow<List<EventInfo>> = _events

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun fetchEvents() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                Log.d("Invites", "Fetching events for user: ${userRepository.getUser().user_id}")
                // Execute the network call on the IO dispatcher
                val eventItems = withContext(Dispatchers.IO) {
                    eventRepository.getUserEventsPending()
                }
                _events.value = eventItems
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage ?: "An unknown error occurred"
                Log.e("HomePageViewModel", "Error fetching events", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun acceptEvent(eventId: String) {
        viewModelScope.launch {
            try {
                eventRepository.acceptEvent(eventId)
                fetchEvents()
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage ?: "An unknown error occurred"
                Log.e("EventInvitationViewModel", "Error accepting event", e)
            }
        }
    }

    fun declineEvent(eventId: String) {
        viewModelScope.launch {
            try {
                eventRepository.declineEvent(eventId)
                fetchEvents()
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage ?: "An unknown error occurred"
                Log.e("EventInvitationViewModel", "Error declining event", e)
            }
        }
    }
}
