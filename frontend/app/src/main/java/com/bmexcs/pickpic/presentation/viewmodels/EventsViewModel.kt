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
import com.bmexcs.pickpic.data.models.EventInfo
import com.bmexcs.pickpic.data.models.ImageInfo
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

@HiltViewModel
class EventsViewModel @Inject constructor(
    private val eventRepository: EventRepository,
    private val imageRepository: ImageRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _images = MutableStateFlow<Map<ImageInfo, ByteArray?>>(emptyMap())
    val images: StateFlow<Map<ImageInfo, ByteArray?>> = _images

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading

    private val _saved = MutableStateFlow(false)
    val saved = _saved

    private val _eventInfo = MutableStateFlow(EventInfo())
    val event = _eventInfo

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage

    init {
        _eventInfo.value = eventRepository.event.value
        viewModelScope.launch {
            getImagesByEventId(event.value.event_id)
        }.invokeOnCompletion {
            startAutoRefresh()
        }
    }

    fun refresh() {
        viewModelScope.launch {
            refreshInternal()
        }
    }

    fun isCurrentUserOwner(ownerId: String): Boolean {
        return userRepository.getUser().user_id == ownerId
    }

    fun isUserPhotoUploader(info: ImageInfo): Boolean {
        return userRepository.getUser().user_id == info.image.image_id
    }

    fun addImage(imageByte: ByteArray) {
        viewModelScope.launch(Dispatchers.IO) {
            imageRepository.addImageBinary(event.value.event_id, imageByte)
        }
    }

    fun deleteImage(eventId: String, imageId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            imageRepository.deleteImage(eventId, imageId)
        }
    }

    fun uriToByteArray(context: Context, uri: Uri?): ByteArray? {
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

    fun saveImageFromByteArrayToGallery(context: Context, byteArray: ByteArray, imageName: String): Boolean {
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

    fun downloadAlbum(context: Context, images: List<Pair<ImageInfo, ByteArray?>>) {
        Log.d(TAG, "Downloading album...")
        viewModelScope.launch(Dispatchers.IO) {
            var successCount = 0
            var failureCount = 0

            images.forEach { (imageInfo, byteArray) ->
                byteArray?.let {
                    val imageName = "event_${event.value.event_id}_${imageInfo.image.image_id}.jpg"
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
                if (eventRepository.isUpdated(event.value.event_id)) {
                    refreshInternal()
                }
            }
        }
    }

    private suspend fun refreshInternal() {
        Log.d(TAG, "Refreshing events page...")
        getImagesByEventId(event.value.event_id)
    }

    private suspend fun getImagesByEventId(eventId: String) {
        _isLoading.value = true

        val images = eventRepository.getAllImageInfo(eventId)

        val imageBitmapList = mutableMapOf<ImageInfo, ByteArray?>()

        for (image in images) {
            val byteArray = imageRepository.getImageByImageId(eventId, image.image.image_id)
            imageBitmapList[image] = byteArray
        }

        _images.value = imageBitmapList

        _isLoading.value = false
    }
}
