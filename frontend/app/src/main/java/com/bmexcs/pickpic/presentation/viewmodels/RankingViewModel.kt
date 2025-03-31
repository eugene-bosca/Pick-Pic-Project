package com.bmexcs.pickpic.presentation.viewmodels

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bmexcs.pickpic.data.models.EventMetadata
import com.bmexcs.pickpic.data.models.ImageMetadata
import com.bmexcs.pickpic.data.repositories.EventRepository
import com.bmexcs.pickpic.data.repositories.ImageRepository
import com.bmexcs.pickpic.data.models.VoteKind
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "RankingViewModel"

data class BitmapWithID(
    val id: String,
    val bitmap: Bitmap,
)

@HiltViewModel
class RankingViewModel @Inject constructor(
    private val eventRepository: EventRepository,
    private val imageRepository: ImageRepository
) : ViewModel() {

    private val _event = MutableStateFlow(EventMetadata())
    val event = _event

    private val _currentImage = MutableStateFlow<BitmapWithID?>(null)
    val currentImage = _currentImage

    private val _nextImage = MutableStateFlow<BitmapWithID?>(null) // Added next image
    val nextImage = _nextImage

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading

    private val unrankedImageInfo = MutableStateFlow<ArrayDeque<ImageMetadata>>(ArrayDeque())

    enum class SwipeDirection {
        LEFT,
        RIGHT;
    }

    init {
        _event.value = eventRepository.event.value
        viewModelScope.launch {
            Log.d(TAG, "Creating unranked image queue, size = ${unrankedImageInfo.value.size}")
            unrankedImageInfo.value = ArrayDeque(eventRepository.getUnrankedImagesMetadata())
        }.invokeOnCompletion {
            loadFirstImages() // Load two images initially
        }
    }

    fun onSwipe(direction: SwipeDirection) {
        if (_currentImage.value == null && unrankedImageInfo.value.isEmpty()) {
            _isLoading.value = false
            return
        }

        viewModelScope.launch {
            _isLoading.value = true

            val voteKind = if (direction == SwipeDirection.LEFT) {
                VoteKind.DOWNVOTE
            } else {
                VoteKind.UPVOTE
            }

            Log.d(TAG, "Voting on imageId = ${_currentImage.value!!.id} with vote = $voteKind")
            eventRepository.voteOnImage(_currentImage.value!!.id, voteKind)

            _currentImage.value = _nextImage.value // Show the pre-fetched image
        }.invokeOnCompletion {
            loadNextImage() // Start loading the subsequent image

            _isLoading.value = false
        }
    }

    private fun loadFirstImages() {
        Log.d(TAG, "Loading first two images")

        viewModelScope.launch {
            _isLoading.value = true

            val first = unrankedImageInfo.value.removeFirstOrNull() ?: run {
                Log.d(TAG, "Tried to load first image but unranked image queue empty")
                _currentImage.value = null
                _isLoading.value = false
                return@launch
            }

            val second = unrankedImageInfo.value.removeFirstOrNull() ?: run {
                Log.d(TAG, "Tried to load second image but unranked image queue empty")
                _nextImage.value = null
                _isLoading.value = false
                return@launch
            }

            val firstByteArray = imageRepository.getImage(
                _event.value.id,
                first.id
            ) ?: throw Exception("First image does not exist")

            val secondByteArray = imageRepository.getImage(
                _event.value.id,
                second.id
            ) ?: throw Exception("Second image does not exist")

            val firstBitmap = BitmapFactory.decodeByteArray(firstByteArray, 0, firstByteArray.size)
            val secondBitmap = BitmapFactory.decodeByteArray(secondByteArray, 0, secondByteArray.size)

            _currentImage.value = BitmapWithID(first.id, firstBitmap)
            _nextImage.value = BitmapWithID(second.id, secondBitmap)

            _isLoading.value = false
        }
    }

    private fun loadNextImage() {
        val next = unrankedImageInfo.value.removeFirstOrNull() ?: run {
            Log.d(TAG, "Unranked image queue empty")
            _nextImage.value = null
            return
        }

        viewModelScope.launch {
            val imageId = next.id

            val byteArray = imageRepository.getImage(
                _event.value.id,
                imageId
            ) ?: throw Exception("Image does not exist")

            val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
            _nextImage.value = BitmapWithID(imageId, bitmap)
        }
    }
}
