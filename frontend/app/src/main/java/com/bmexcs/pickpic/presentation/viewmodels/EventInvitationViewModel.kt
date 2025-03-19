package com.bmexcs.pickpic.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bmexcs.pickpic.data.dtos.EventInfo
import com.bmexcs.pickpic.data.repositories.EventRepository
import com.bmexcs.pickpic.data.repositories.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "EventInvitationViewModel"

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

    fun loadEvents() {
        viewModelScope.launch {
            fetchEvents()
        }
    }

    fun acceptEvent(eventId: String) {
        viewModelScope.launch {
            try {
                eventRepository.acceptEvent(eventId)
                fetchEvents()
            } catch (e: Exception) {
                Log.e(TAG, "Error accepting event", e)
                _errorMessage.value = e.localizedMessage ?: "An unknown error occurred"
            }
        }
    }

    fun declineEvent(eventId: String) {
        viewModelScope.launch {
            try {
                eventRepository.declineEvent(eventId)
                fetchEvents()
            } catch (e: Exception) {
                Log.e(TAG, "Error declining event", e)
                _errorMessage.value = e.localizedMessage ?: "An unknown error occurred"
            }
        }
    }

    private suspend fun fetchEvents() {
        _isLoading.value = true
        try {
            Log.d(TAG, "Fetching events for user: ${userRepository.getUser().user_id}")

            val eventItems = eventRepository.getPendingEventsMetadata()

            _events.value = eventItems
            _errorMessage.value = null

        } catch (e: Exception) {
            Log.e(TAG, "Error fetching events", e)
            _errorMessage.value = e.localizedMessage ?: "An unknown error occurred"

        } finally {
            _isLoading.value = false
        }
    }
}
