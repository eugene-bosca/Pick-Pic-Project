package com.bmexcs.pickpic.presentation.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bmexcs.pickpic.data.models.EventInfo
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
    private val _eventInfo = MutableStateFlow<EventInfo?>(null)
    val eventInfo: StateFlow<EventInfo?> = _eventInfo

    var eventName = mutableStateOf("")

    fun onCreate(onSuccess: () -> Unit, onError: (String) -> Unit) {
        val name = eventName.value.trim()
        if (name.isEmpty()) {
            onError("Event name cannot be empty")
            return
        }

        viewModelScope.launch {
            try {
                _eventInfo.value = eventRepository.createEvent(name)
                onSuccess()
            } catch (e: Exception) {
                onError("Error: ${e.localizedMessage}")
            }
        }
    }
}
