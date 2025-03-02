package com.bmexcs.pickpic.presentation.viewmodels

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bmexcs.pickpic.data.models.Image
import com.bmexcs.pickpic.data.repositories.AuthRepository
import com.bmexcs.pickpic.data.repositories.EventsRepository
import com.bmexcs.pickpic.data.repositories.ImageRepository
import com.bmexcs.pickpic.data.serializable.SerializableUUID
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

    private val currEventId = "41cc19ac-3ce4-4562-861d-871750cc4d6f"

    private val _user = MutableStateFlow<String?>("")
    private val user = _user

    init {
        getImageByEventId(currEventId)
        _user.value = authRepository.getCurrentUser()?.uid
    }

    private fun getImageByEventId(eventId: String) {
        // Launch a coroutine on the IO dispatcher since this is a network request.
        viewModelScope.launch(Dispatchers.IO) {
            val images = eventsRepository.getImageByEventId(eventId)
            val imageBitmapList = mutableListOf<Bitmap?>()

            for(image in images) {
                imageBitmapList.add(imageRepository.getImageByImageId(image.id))
            }

            _images.value = imageBitmapList
        }
    }

    private fun addImageByEventId(image: Image) {
        // Launch a coroutine on the IO dispatcher since this is a network request.
        viewModelScope.launch(Dispatchers.IO) {
            eventsRepository.addImageByEventId(image)
        }
    }

    private fun deleteImageByEventId(imageId: String) {
        // Launch a coroutine on the IO dispatcher since this is a network request.
        viewModelScope.launch(Dispatchers.IO) {
            eventsRepository.deleteImageByEventId(imageId)
        }
    }
}
