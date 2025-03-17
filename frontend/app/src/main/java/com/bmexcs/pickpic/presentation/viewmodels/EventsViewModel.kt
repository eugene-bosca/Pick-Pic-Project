package com.bmexcs.pickpic.presentation.viewmodels

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bmexcs.pickpic.data.models.EventInfo
import com.bmexcs.pickpic.data.repositories.EventRepository
import com.bmexcs.pickpic.data.repositories.ImageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import javax.inject.Inject

@HiltViewModel
class EventsViewModel @Inject constructor(
    private val eventRepository: EventRepository,
    private val imageRepository: ImageRepository,
) : ViewModel() {

    private val _images = MutableStateFlow<Map<String, ByteArray?>>(emptyMap())
    val images: StateFlow<Map<String, ByteArray?>> = _images

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading

    private val _saved = MutableStateFlow(false)
    val saved = _saved

    private val _eventInfo = MutableStateFlow(EventInfo())
    val event = _eventInfo

    init {
        _eventInfo.value = eventRepository.event.value
        getImagesByEventId(event.value.event_id)
    }

    fun addImage(imageByte: ByteArray) {
        viewModelScope.launch(Dispatchers.IO) {
            imageRepository.addImageBinary(event.value.event_id, imageByte)
        }.invokeOnCompletion({
            getImagesByEventId(event.value.event_id)
        })
    }

    fun deleteImage(eventId: String, imageId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            imageRepository.deleteImage(eventId, imageId)
        }.invokeOnCompletion({
            getImagesByEventId(event.value.event_id)
        })
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
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)

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
            put(MediaStore.Images.Media.DISPLAY_NAME, imageName) // Image name
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg") // Image type (could be "image/png")
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures") // Location to save (e.g., Pictures folder)
        }

        // Get content resolver to insert image into MediaStore
        val resolver = context.contentResolver
        val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

        return if (imageUri != null) {
            // Open output stream to write the image file
            resolver.openOutputStream(imageUri)?.use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream) // Save as JPEG
            }
            true // Image saved successfully
        } else {
            false // Failed to insert into MediaStore
        }
    }

    private fun getImagesByEventId(eventId: String) {
        _isLoading.value = true

        // Launch a coroutine on the IO dispatcher since this is a network request.
        viewModelScope.launch(Dispatchers.IO) {
            val images = eventRepository.getImages(eventId)
            val imageBitmapList = mutableMapOf<String, ByteArray?>()

            for (image in images) {
                val byteArray = imageRepository.getImageByImageId(eventId, image.image.image_id)
                imageBitmapList.put(image.image.image_id, byteArray)
            }

            _images.value = imageBitmapList
        }.invokeOnCompletion { _isLoading.value = false }
    }
}
