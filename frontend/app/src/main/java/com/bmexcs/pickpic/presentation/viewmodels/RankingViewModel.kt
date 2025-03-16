package com.bmexcs.pickpic.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bmexcs.pickpic.data.models.BitmapRanked
import com.bmexcs.pickpic.data.repositories.EventRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "RankingViewModel"

@HiltViewModel
class RankingViewModel @Inject constructor(
    private val eventRepository: EventRepository,
) : ViewModel() {

    private val _currentImage = MutableStateFlow<BitmapRanked?>(null)
    val currentImage: StateFlow<BitmapRanked?> = _currentImage

    enum class SwipeDirection { LEFT, RIGHT }

    init {
        loadNextImage()
    }

    private fun loadNextImage() {
        viewModelScope.launch {
            Log.d(TAG, "Loading next image...")
            val image = eventRepository.getUnrankedImage()
            Log.d(TAG, "Received image ${image.info.image.image_id}")
            _currentImage.value = image
        }
    }

    fun onSwipe(direction: SwipeDirection) {
        val score = swipeDirectionToScore(direction)
        loadNextImage()
    }

    private fun swipeDirectionToScore(direction: SwipeDirection): Long {
        return if (direction == SwipeDirection.LEFT) {
            -1
        } else {
            1
        }
    }
}
