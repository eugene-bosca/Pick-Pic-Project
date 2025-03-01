package com.bmexcs.pickpic.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bmexcs.pickpic.data.repositories.EventsRepository // Assuming you have an EventsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventInvitationViewModel @Inject constructor(
    private val eventRepository: EventsRepository // Inject EventsRepository
) : ViewModel() {

    // State to track loading and error states
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun acceptEvent(eventId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            try {
                eventRepository.addUserToEvent(eventId) // Assuming this function exists
                _isLoading.value = false
            } catch (e: Exception) {
                _errorMessage.value = "Failed to accept event: ${e.message}"
                _isLoading.value = false
            }
        }
    }
}