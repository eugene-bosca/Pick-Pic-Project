package com.bmexcs.pickpic.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bmexcs.pickpic.data.repositories.EventsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventInvitationViewModel @Inject constructor(
    private val eventRepository: EventsRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)

    private val _errorMessage = MutableStateFlow<String?>(null)

    // State to hold event details
    private val _eventName = MutableStateFlow<String?>(null)
    val eventName: StateFlow<String?> = _eventName

    private val _eventId = MutableStateFlow<String?>(null)

    fun acceptEvent() {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            try {
                _eventId.value?.let { eventRepository.addUserToEvent(it) }
                _isLoading.value = false
            } catch (e: Exception) {
                _errorMessage.value = "Failed to accept event: ${e.message}"
                _isLoading.value = false
            }
        }
    }
}
