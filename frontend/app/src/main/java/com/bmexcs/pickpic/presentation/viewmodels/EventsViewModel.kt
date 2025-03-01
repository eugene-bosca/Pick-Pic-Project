package com.bmexcs.pickpic.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bmexcs.pickpic.data.models.Image
import com.bmexcs.pickpic.data.repositories.AuthRepository
import com.bmexcs.pickpic.data.repositories.EventsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventsViewModel @Inject constructor(
    private val eventRepository: EventsRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    // Backing property for the dog images list
    private val _images = MutableStateFlow<List<String>>(emptyList())
    val images: StateFlow<List<String>> = _images

    private val currEventId = 0;

    init {
        if (_images.value.isEmpty()){
            // Get Auth user so I can pass the event to the image
            getImageByEventId(currEventId)
        }
    }

    private fun getImageByEventId(eventId: Int) {
        // Launch a coroutine on the IO dispatcher since this is a network request.
        viewModelScope.launch(Dispatchers.IO) {
            _images.value = eventRepository.getImageByEventId(eventId)
        }
    }

    private fun addImageByEventId(image: Image) {
        // Launch a coroutine on the IO dispatcher since this is a network request.
        viewModelScope.launch(Dispatchers.IO) {
            eventRepository.addImageByEventId(image)
        }
    }

    private fun deleteImageByEventId(image: Image) {
        // Launch a coroutine on the IO dispatcher since this is a network request.
        viewModelScope.launch(Dispatchers.IO) {
            eventRepository.deleteImageByEventId(image)
        }
    }
}
