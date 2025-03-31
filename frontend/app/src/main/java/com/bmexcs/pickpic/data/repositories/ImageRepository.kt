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

    private val _imagesCache = MutableStateFlow<Map<String,ByteArray>>(emptyMap())
    val imagesCache: StateFlow<Map<String,ByteArray>> = _imagesCache

    fun setPrevEvent(eventInfo: EventMetadata) {
        _prevEvent.value = eventInfo
    }

    fun setImageCache(imageId: String, byteArray: ByteArray) {
        _imagesCache.value += mapOf(imageId to byteArray)
    }

    fun clearImageCache() {
        _imagesCache.value = emptyMap()
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
