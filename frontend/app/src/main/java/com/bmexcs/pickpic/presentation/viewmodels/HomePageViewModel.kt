package com.bmexcs.pickpic.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bmexcs.pickpic.data.models.EventMetadata
import com.bmexcs.pickpic.data.repositories.EventRepository
import com.bmexcs.pickpic.data.repositories.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "HomePageViewModel"

@HiltViewModel
class HomePageViewModel @Inject constructor(
    private val eventRepository: EventRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _events = MutableStateFlow<List<EventMetadata>>(emptyList())
    val events: StateFlow<List<EventMetadata>> = _events

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun setEvent(event: EventMetadata) {
        eventRepository.setCurrentEvent(event)
    }

    fun deleteEvent(eventId: String) {
        viewModelScope.launch {
            eventRepository.deleteEvent(eventId)
        }
        // delete the event locally without needing to make a new network call
        _events.value = _events.value.filter { it.id != eventId }
    }

    fun fetchEvents() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                Log.d(TAG, "Fetching events for user: ${userRepository.getUser().id}")

                // Execute the network call on the IO dispatcher
                val eventItems = eventRepository.getAllEventsMetadata()

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

    fun isCurrentUserOwner(ownerId: String): Boolean {
        return userRepository.getUser().id == ownerId
    }
}
