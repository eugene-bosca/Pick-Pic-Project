package com.bmexcs.pickpic.presentation.viewmodels

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
import java.io.IOException
import java.io.InputStream
import java.util.Arrays
import javax.inject.Inject


@HiltViewModel
class EventsViewModel @Inject constructor(
    private val eventsRepository: EventsRepository,
    private val imageRepository: ImageRepository,
) : ViewModel() {

    // Backing property for the dog images list
    private val _images = MutableStateFlow<List<ByteArray?>>(emptyList())
    val images: StateFlow<List<ByteArray?>> = _images

    private val _event = MutableStateFlow<Event>(Event())
    val event = _event

    fun setEvent(event: Event) {
        eventsRepository.event.value = event
    }

    fun getEventFromRepository () {
        _event.value = eventsRepository.event.value
    }

    fun initializeEventsScreenView() {
        getEventFromRepository()
        getImageByEventId(event.value.event_id)
    }

    fun getImageByEventId(eventId: String) {
        // Launch a coroutine on the IO dispatcher since this is a network request.
        viewModelScope.launch(Dispatchers.IO) {
            val images = eventsRepository.getImageByEventId(eventId)
            val imageBitmapList = mutableListOf<ByteArray?>()

            for(image in images) {
                val byteArray = imageRepository.getImageByImageId(image.event.event_id, image.image.image_id)
                imageBitmapList.add(byteArray)
            }


            _images.value = imageBitmapList
        }
    }

    fun addImageByEvent(imageByte: ByteArray) {
        // Launch a coroutine on the IO dispatcher since this is a network request.
        viewModelScope.launch(Dispatchers.IO) {
            // replace static id with an actual id
            val eventPicture = imageRepository.addImageBinary(event.value.event_id, imageByte)
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
}
