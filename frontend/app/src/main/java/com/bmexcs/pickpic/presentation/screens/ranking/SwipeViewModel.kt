package com.bmexcs.pickpic.presentation.ranking

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SwipeViewModel : ViewModel() {

    private val _currentBitmap = MutableLiveData<Bitmap?>()
    val currentBitmap: LiveData<Bitmap?> = _currentBitmap

    private val _swipeDirection = MutableLiveData<SwipeDirection>()

    private var bitmaps: List<Bitmap> = emptyList()
    private var currentImageIndex = 0

    fun initializeBitmaps(context: Context) {
        viewModelScope.launch {
            bitmaps = withContext(Dispatchers.IO) {
                loadBitmapsFromAssets(context, "mockImages")
            }
            _currentBitmap.value = bitmaps.firstOrNull() // Set initial image
        }
    }

    private suspend fun loadBitmapsFromAssets(context: Context, folderName: String): List<Bitmap> =
        withContext(Dispatchers.IO) {
            try {
                val assetManager = context.assets
                val files = assetManager.list(folderName) ?: emptyArray()
                files.mapNotNull { fileName ->
                    try {
                        val inputStream = assetManager.open("$folderName/$fileName")
                        BitmapFactory.decodeStream(inputStream)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        null
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
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