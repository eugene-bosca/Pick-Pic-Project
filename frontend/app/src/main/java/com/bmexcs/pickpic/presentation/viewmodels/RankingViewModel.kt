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
            loadFirstImage()
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
                VoteKind.UPVOTE
            } else {
                VoteKind.DOWNVOTE
            }

            Log.d(TAG, "Voting on imageId = ${_currentImage.value!!.id} with vote = $voteKind")
            eventRepository.voteOnImage(_currentImage.value!!.id, voteKind)

            val next = unrankedImageInfo.value.removeFirstOrNull() ?: run {
                Log.d(TAG, "Unranked image queue empty")
                _currentImage.value = null
                _isLoading.value = false
                return@launch
            }

            Log.d(TAG, "Loading next image")
            val imageId = next.id

            val byteArray = imageRepository.getImage(
                _event.value.id,
                imageId
            ) ?: throw Exception("Image does not exist")

            val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
            _currentImage.value = BitmapWithID(imageId, bitmap)

            _isLoading.value = false
        }
    }

    private fun loadFirstImage() {
        Log.d(TAG, "Loading first image")

        val next = unrankedImageInfo.value.removeFirstOrNull() ?: run {
            Log.d(TAG, "Tried to load first image but unranked image queue empty")
            _currentImage.value = null
            _isLoading.value = false
            return
        }

        viewModelScope.launch {
            _isLoading.value = true

            val imageId = next.id

            val byteArray = imageRepository.getImage(
                _event.value.id,
                imageId
            ) ?: throw Exception("Image does not exist")

            val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)

            _currentImage.value = BitmapWithID(imageId, bitmap)

            _isLoading.value = false
        }
    }
}
