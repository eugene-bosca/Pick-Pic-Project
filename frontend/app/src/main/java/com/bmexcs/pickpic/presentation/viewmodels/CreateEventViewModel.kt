package com.bmexcs.pickpic.presentation.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bmexcs.pickpic.data.repositories.EventRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateEventViewModel @Inject constructor(
    private val eventRepository: EventRepository
) : ViewModel() {

    var eventNameInput = mutableStateOf("")

    fun onCreate(onSuccess: () -> Unit, onError: (String) -> Unit) {
        val name = eventNameInput.value.trim()
        if (name.isEmpty()) {
            onError("Event name cannot be empty")
            return
        }

        viewModelScope.launch {
            try {
                eventRepository.createEvent(name)
                onSuccess()
            } catch (e: Exception) {
                onError("Error: ${e.localizedMessage}")
            }
        }
    }
}
