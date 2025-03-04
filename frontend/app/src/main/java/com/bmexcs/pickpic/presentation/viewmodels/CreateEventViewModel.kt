package com.bmexcs.pickpic.presentation.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bmexcs.pickpic.data.models.CreateEvent
import com.bmexcs.pickpic.data.repositories.EventRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateEventViewModel @Inject constructor(
    private val eventRepository: EventRepository
) : ViewModel() {
    private val _event = MutableStateFlow<CreateEvent?>(null)
    val event: StateFlow<CreateEvent?> = _event

    var eventName = mutableStateOf("")

    fun onCreate(onSuccess: () -> Unit, onError: (String) -> Unit) {
        val name = eventName.value.trim()
        if (name.isEmpty()) {
            onError("Event name cannot be empty")
            return
        }

        viewModelScope.launch {
            try {
                _event.value = eventRepository.createEvent(name)
                onSuccess()
            } catch (e: Exception) {
                onError("Error: ${e.localizedMessage}")
            }
        }
    }
}
