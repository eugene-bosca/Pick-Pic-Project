package com.bmexcs.pickpic.presentation.viewmodels

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bmexcs.pickpic.data.models.EventContent
import com.bmexcs.pickpic.data.repositories.AuthRepository
import com.bmexcs.pickpic.data.repositories.EventsRepository
import com.bmexcs.pickpic.data.repositories.ImageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventsViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val eventsRepository: EventsRepository,
    private val imageRepository: ImageRepository
) : ViewModel() {

    // Backing property for the dog images list
    private val _images = MutableStateFlow<List<Bitmap?>>(emptyList())
    val images: StateFlow<List<Bitmap?>> = _images

    private val _event = MutableStateFlow<String>("")
    private val event = _event

    private val _user = MutableStateFlow<String?>("")
    private val user = _user

    init {
        _event.value = ""
        _user.value = authRepository.getCurrentUser()?.uid

        getImageByEventId(event.value)
    }

    fun getImageByEventId(eventId: String) {
        // Launch a coroutine on the IO dispatcher since this is a network request.
        viewModelScope.launch(Dispatchers.IO) {
            val images = eventsRepository.getImageByEventId(eventId)
            val imageBitmapList = mutableListOf<Bitmap?>()

            for(image in images) {
                imageBitmapList.add(imageRepository.getImageByImageId(image.image_id.image_id))
            }

            _images.value = imageBitmapList
        }
    }

    fun addImageByEventId(eventContent: EventContent) {
        // Launch a coroutine on the IO dispatcher since this is a network request.
        viewModelScope.launch(Dispatchers.IO) {
            eventsRepository.addImageByEventId(eventContent)
        }
    }

    fun deleteImageByEventId(imageId: String) {
        // Launch a coroutine on the IO dispatcher since this is a network request.
        viewModelScope.launch(Dispatchers.IO) {
            eventsRepository.deleteImageByEventId(imageId)
        }
    }
}
