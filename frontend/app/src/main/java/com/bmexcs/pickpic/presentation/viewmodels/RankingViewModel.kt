package com.bmexcs.pickpic.presentation.viewmodels

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bmexcs.pickpic.data.repositories.EventRepository
import com.bmexcs.pickpic.data.repositories.ImageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class RankingViewModel @Inject constructor(
    private val eventRepository: EventRepository,
    private val imageRepository: ImageRepository
) : ViewModel() {
    private val _currentBitmap = MutableLiveData<Bitmap?>()
    val currentBitmap: LiveData<Bitmap?> = _currentBitmap

    private val _swipeDirection = MutableLiveData<SwipeDirection>()

    private var bitmaps: List<Bitmap> = emptyList()
    private var currentImageIndex = 0

    fun initializeBitmaps(context: Context) {
        viewModelScope.launch {
            bitmaps = withContext(Dispatchers.IO) {
                loadBitmaps(context)
            }
            _currentBitmap.value = bitmaps.firstOrNull()
        }
    }

    private suspend fun loadBitmaps(context: Context): List<Bitmap> =
        withContext(Dispatchers.IO) {
            val eventId = eventRepository.event.value.event_id
            val imageInfos = eventRepository.getImages(eventId)
            imageInfos.mapNotNull { imageInfo ->
                val bytes = imageRepository.getImageByImageId(eventId, imageInfo.image.image_id)
                bytes?.let { BitmapFactory.decodeByteArray(it, 0, it.size) }
            }
        }

    fun onSwipe(direction: SwipeDirection) {
        _swipeDirection.value = direction
        currentImageIndex = when (direction) {
            SwipeDirection.LEFT -> {
                (currentImageIndex - 1 + bitmaps.size) % bitmaps.size
            }

            SwipeDirection.RIGHT -> {
                (currentImageIndex + 1) % bitmaps.size
            }
        }
        _currentBitmap.value = bitmaps.getOrNull(currentImageIndex)
    }
    enum class SwipeDirection { LEFT, RIGHT }
}
