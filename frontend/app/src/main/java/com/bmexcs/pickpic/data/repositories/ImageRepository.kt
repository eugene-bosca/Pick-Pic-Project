package com.bmexcs.pickpic.data.repositories

import com.bmexcs.pickpic.data.models.EventMetadata
import com.bmexcs.pickpic.data.models.Image
import com.bmexcs.pickpic.data.sources.ImageDataSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImageRepository @Inject constructor(
    private val imageDataSource: ImageDataSource
) {

    private val _prevEvent = MutableStateFlow(EventMetadata())
    val prevEvent = _prevEvent

    private val _imagesCache = MutableStateFlow<List<Image>>(emptyList())
    val imagesCache: StateFlow<List<Image>> = _imagesCache

    fun setPrevEvent(eventInfo: EventMetadata) {
        _prevEvent.value = eventInfo
    }

    fun setImages(images: List<Image>) {
        _imagesCache.value = images
    }

    suspend fun getImage(eventId: String, imageId: String): ByteArray? {
        return imageDataSource.getImage(eventId, imageId);
    }

    suspend fun addImage(eventId: String, imageByte: ByteArray) {
        return imageDataSource.addImage(eventId, imageByte)
    }

    suspend fun deleteImage(eventId: String, imageId: String) {
        imageDataSource.deleteImage(eventId, imageId)
    }
}
