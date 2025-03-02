package com.bmexcs.pickpic.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bmexcs.pickpic.data.repositories.EventsRepository
import com.bmexcs.pickpic.data.repositories.ImageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class EventsViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val eventsRepository: EventsRepository,
    private val imageRepository: ImageRepository
) : ViewModel() {

    // Backing property for the dog images list
    private val _dogImages = MutableStateFlow<List<String>>(emptyList())
    val dogImages: StateFlow<List<String>> = _dogImages

    private val _user = MutableStateFlow<String?>("")
    private val user = _user

    init {
        if (_dogImages.value.isEmpty()){
            fetchDogImages()
        }

        _user.value = authRepository.getCurrentUser()?.uid
    }

    private fun fetchDogImages() {
        // Launch a coroutine on the IO dispatcher since this is a network request.
        viewModelScope.launch(Dispatchers.IO) {
            var images = eventsRepository.getImageByEventId(eventId)

            for(image in images) {
                _images.value = imageRepository.getImageByImageId(image.id)
            }
        }
    }

    private fun addImageByEventId(image: Image) {
        // Launch a coroutine on the IO dispatcher since this is a network request.
        viewModelScope.launch(Dispatchers.IO) {
            eventsRepository.addImageByEventId(image)
        }
    }

    private fun deleteImageByEventId(image: Image) {
        // Launch a coroutine on the IO dispatcher since this is a network request.
        viewModelScope.launch(Dispatchers.IO) {
            eventsRepository.deleteImageByEventId(image)
        }
    }
}
