package com.bmexcs.pickpic.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bmexcs.pickpic.data.utils.BitmapRanked
import com.bmexcs.pickpic.data.models.EventInfo
import com.bmexcs.pickpic.data.repositories.EventRepository
import com.bmexcs.pickpic.data.utils.Vote
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

    private val _eventInfo = MutableStateFlow(EventInfo())
    val event = _eventInfo

    enum class SwipeDirection {
        LEFT,
        RIGHT;
    }

    init {
        _eventInfo.value = eventRepository.event.value
        loadNextImage()
    }

    fun onSwipe(direction: SwipeDirection) {
        viewModelScope.launch {
            if (currentImage.value != null) {
                val imageId = currentImage.value!!.info.image.image_id

                val vote = if (direction == SwipeDirection.LEFT) {
                    Vote.UPVOTE
                } else {
                    Vote.DOWNVOTE
                }

                eventRepository.voteOnImage(imageId, vote)

                Log.d(TAG, "imageId = ${imageId}, vote = $vote")
            }
        }.invokeOnCompletion {
            loadNextImage()
        }
    }

    fun onSkip() {
        viewModelScope.launch {
            loadNextImage()
        }
    }

    private fun loadNextImage() {
        viewModelScope.launch {
            Log.d(TAG, "Loading next image...")
            val image = eventRepository.getUnrankedImage()
            Log.d(TAG, "Received image ${image.info.image.image_id}")
            _currentImage.value = image
        }
    }
}
