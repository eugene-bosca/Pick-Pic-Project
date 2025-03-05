package com.bmexcs.pickpic.presentation.viewmodels

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bmexcs.pickpic.data.models.Event
import com.bmexcs.pickpic.data.models.EventContent
import com.bmexcs.pickpic.data.models.Image
import com.bmexcs.pickpic.data.repositories.EventsRepository
import com.bmexcs.pickpic.data.repositories.ImageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import javax.inject.Inject


@HiltViewModel
class EventsViewModel @Inject constructor(
    private val eventsRepository: EventsRepository,
    private val imageRepository: ImageRepository,
) : ViewModel() {

    // Backing property for the dog images list
    private val _images = MutableStateFlow<List<String?>>(emptyList())
    val images: StateFlow<List<String?>> = _images

    private val _event = MutableStateFlow<Event>(Event())
    val event = _event

    private val _eventContent = MutableStateFlow<EventContent?>(null)
    val eventContent = _eventContent

    fun setEvent(event: Event) {
        _event.value = event
    }

    fun getImageByEventId(eventId: String) {
        // Launch a coroutine on the IO dispatcher since this is a network request.
        viewModelScope.launch(Dispatchers.IO) {
            val images = eventsRepository.getImageByEventId(eventId)
            val imageBitmapList = mutableListOf<String?>()

            for(image in images) {
                imageBitmapList.add(imageRepository.getImageByImageId(image.image_id.image_id))
            }

            _images.value = imageBitmapList
        }
    }

    fun addImageByEvent(imageByte: ByteArray?) {
        // Launch a coroutine on the IO dispatcher since this is a network request.
        viewModelScope.launch(Dispatchers.IO) {
            val imageId = imageRepository.addImageBinary("035f8345-a25c-45f2-a88f-d994f4cfa667", imageByte)
        }
    }

    fun deleteImageByEventId(imageId: String) {
        // Launch a coroutine on the IO dispatcher since this is a network request.
        viewModelScope.launch(Dispatchers.IO) {
            eventsRepository.deleteImageByEventId(imageId)
        }
    }

    fun uriToByteArray(context: Context, uri: Uri?): ByteArray? {
        var inputStream: InputStream? = null
        var byteArray: ByteArray? = null

        try {
            if(uri == null) {
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
}
