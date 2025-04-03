package com.bmexcs.pickpic.presentation.viewmodels

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bmexcs.pickpic.data.models.EventMetadata
import com.bmexcs.pickpic.data.models.Image
import com.bmexcs.pickpic.data.models.ImageMetadata
import com.bmexcs.pickpic.data.repositories.EventRepository
import com.bmexcs.pickpic.data.repositories.ImageRepository
import com.bmexcs.pickpic.data.repositories.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import javax.inject.Inject

private const val TAG = "EventsViewModel"

enum class FilterType {
    FilterDateDesc,
    FilterDateAsc,
    FilterRankDesc,
    FilterRankAsc
}

enum class DownloadAmount {
    All,
    TopTen,
    TopTwenty
}

@HiltViewModel
class EventsViewModel @Inject constructor(
    private val eventRepository: EventRepository,
    private val imageRepository: ImageRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _images = MutableStateFlow<List<Image>>(emptyList())
    val images: StateFlow<List<Image>> = _images

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading

    private val _saved = MutableStateFlow(false)
    val saved = _saved

    private val _event = MutableStateFlow(EventMetadata())
    val event = _event

    private val _filterType = MutableStateFlow(FilterType.FilterDateDesc)
    val filterType = _filterType

    private val _downloadAmount = MutableStateFlow(DownloadAmount.All)
    val downloadAmount = _downloadAmount

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage

    private val _selectedImages = MutableStateFlow<Set<String>>(emptySet())
    val selectedImages: StateFlow<Set<String>> = _selectedImages

    init {
        _event.value = eventRepository.event.value
        viewModelScope.launch {
            getImagesByEventId(event.value.id)
        }.invokeOnCompletion {
            startAutoRefresh()
        }
    }

    private fun setPrevEvent(event: EventMetadata) {
        imageRepository.setPrevEvent(event)
    }

    fun refresh() {
        viewModelScope.launch {
            refreshInternal()
        }
    }

    fun isCurrentUserOwner(ownerId: String): Boolean {
        return userRepository.getUser().id == ownerId
    }

    fun isUserPhotoUploader(imageMetadata: ImageMetadata): Boolean {
        return userRepository.getUser().id == imageMetadata.uploader.id
    }

    fun addImage(context: Context, uri: Uri?) {
        viewModelScope.launch(Dispatchers.IO) {
            uriToByteArray(context, uri)?.let { byteArray ->
                imageRepository.addImage(event.value.id, byteArray)
            }
        }.invokeOnCompletion {
            viewModelScope.launch(Dispatchers.IO) {
                refreshInternal()
            }
        }
    }

    fun deleteImage(eventId: String, imageId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            imageRepository.deleteImage(eventId, imageId)
        }.invokeOnCompletion {
            viewModelScope.launch(Dispatchers.IO) {
                refreshInternal()
            }
        }
    }

    fun clearSelectedImages() {
        _selectedImages.value = emptySet()
    }

    fun downloadSelectedImages(context: Context, selectedImageIds: Set<String>) {
        viewModelScope.launch(Dispatchers.IO) {
            val selectedImages = images.value.filter { it.metadata.id in selectedImageIds }
            downloadAlbum(context, selectedImages)
            clearSelectedImages()
        }
    }

    fun toggleImageSelection(imageId: String) {
        _selectedImages.value = _selectedImages.value.toMutableSet().apply {
            if (contains(imageId)) remove(imageId) else add(imageId)
        }
    }

    private fun uriToByteArray(context: Context, uri: Uri?): ByteArray? {
        var inputStream: InputStream? = null
        var byteArray: ByteArray? = null

        try {
            if (uri == null) {
                return null
            }
            // Open an InputStream from the URI
            inputStream = context.contentResolver.openInputStream(uri)

            // Read the InputStream into a Bitmap
            val bitmap = BitmapFactory.decodeStream(inputStream)

            // Convert the Bitmap to ByteArray
            if (bitmap != null) {
                val byteArrayOutputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)

                byteArray = byteArrayOutputStream.toByteArray()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            inputStream?.close()
        }

        return byteArray
    }

    fun saveImageFromByteArrayToGallery(
        context: Context,
        byteArray: ByteArray,
        imageName: String
    ): Boolean {
        // Convert byteArray to Bitmap
        val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)

        // Prepare ContentValues to specify where to store the image
        val values = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, imageName) // Image name
            put(MediaStore.Downloads.MIME_TYPE, "image/jpeg") // Image type
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                put(MediaStore.Downloads.RELATIVE_PATH, "Download") // Save to Downloads/PickPic
            }
        }

        // Get content resolver to insert image into MediaStore
        val resolver = context.contentResolver
        val imageUri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)

        return if (imageUri != null) {
            // Open output stream to write the image file
            resolver.openOutputStream(imageUri)?.use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream) // Save as JPEG
            }
            // Notify the media scanner
            val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
            mediaScanIntent.data = imageUri
            context.sendBroadcast(mediaScanIntent)
            true // Image saved successfully
        } else {
            false // Failed to insert into MediaStore
        }
    }

    fun downloadAlbum(context: Context, images: List<Image>) {
        Log.d(TAG, "Downloading album...")
        viewModelScope.launch(Dispatchers.IO) {
            var successCount = 0
            var failureCount = 0

            // default all
            var topSubset = sortImages(images, filterType.value)

            if (downloadAmount.value == DownloadAmount.TopTen) {
                topSubset = topSubset.take(10)
            } else if (downloadAmount.value == DownloadAmount.TopTwenty) {
                topSubset = topSubset.take(20)
            }

            topSubset.forEach { image ->
                image.data.let {
                    val imageName = "event_${event.value.id}_${image.metadata.id}.jpg"
                    val isSaved = saveImageFromByteArrayToGallery(context, it, imageName)
                    if (isSaved) {
                        successCount++
                    } else {
                        failureCount++
                    }
                }
            }

            // Show a confirmation message
            val message = when {
                successCount > 0 && failureCount == 0 -> "All $successCount images saved to Downloads/PickPic!"
                successCount > 0 && failureCount > 0 -> "$successCount images saved, $failureCount failed."
                else -> "Failed to save any images."
            }
            withContext(Dispatchers.Main) {
                _snackbarMessage.value = message
            }
        }
        Log.d(TAG, "Download album finished.")
    }

    fun clearSnackbarMessage() {
        _snackbarMessage.value = null
    }

    private fun startAutoRefresh() {
        viewModelScope.launch(Dispatchers.IO) {
            while (isActive) {
                delay(5000)
                if (eventRepository.isUpdated(event.value.id)) {
                    refreshInternal()
                }
            }
        }
    }

    private suspend fun refreshInternal() {
        if (!_isLoading.value) { // Prevent overlapping refreshes
            Log.d(TAG, "Refreshing events page...")
            getImagesByEventId(event.value.id)
        }
    }

    private suspend fun getImagesByEventId(eventId: String) {
        _isLoading.value = true

        // Caching related code
        imageRepository.prevEvent.value.id.let { prevId ->
            if (prevId != eventId) {
                imageRepository.clearImageCache()
            }
        }

        setPrevEvent(event.value)

        val imageMetadata = eventRepository.getAllImagesMetadata(eventId)
        _images.value = emptyList()

        for (metadata in imageMetadata) {
                val byteArray = imageRepository.getImage(event.value.id, metadata.id)

                if (byteArray != null) {
                    _images.value += Image(metadata, byteArray)
                } else {
                    println("Failed to retrieve image with metadata: $metadata")
                }
        }

        val existingIds = images.value.map { it.metadata.id }.toSet()
        _selectedImages.value = _selectedImages.value.filter { it in existingIds }.toSet()

        _images.value = sortImages(_images.value, filterType.value)

        _isLoading.value = false
    }

    private fun sortImages(image: List<Image>, filterType: FilterType): List<Image> {
        return when (filterType) {
            FilterType.FilterDateDesc -> image.sortedByDescending { it.metadata.dateUploaded }
            FilterType.FilterDateAsc -> image.sortedBy { it.metadata.dateUploaded }
            FilterType.FilterRankDesc -> image.sortedByDescending { it.metadata.score }
            FilterType.FilterRankAsc -> image.sortedBy { it.metadata.score }
        }
    }

    fun sortCachedImages() {
        _images.value = sortImages(images.value, filterType.value)
    }

}