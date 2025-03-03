package com.bmexcs.pickpic.presentation.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bmexcs.pickpic.data.models.CreateEvent
import com.bmexcs.pickpic.data.models.User
import com.bmexcs.pickpic.data.serializable.SerializableUUID
import com.bmexcs.pickpic.data.sources.CreateEventDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CreateEventViewModel @Inject constructor(
    private val createEventDataSource: CreateEventDataSource
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

        val newEvent = CreateEvent(event_name = name, owner = UUID.fromString("68e24b1a-36c8-4de5-a751-ba414e77db0b"))
        _event.value = newEvent

        viewModelScope.launch {
            try {
                createEventDataSource.postEvent(newEvent)
                onSuccess()
            } catch (e: Exception) {
                onError("Error: ${e.localizedMessage}")
            }
        }
    }


}