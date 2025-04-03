package com.bmexcs.pickpic.data.repositories

import com.bmexcs.pickpic.data.models.EventMetadata
import com.bmexcs.pickpic.data.sources.AuthDataSource
import com.bmexcs.pickpic.data.sources.ImageDataSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImageRepository @Inject constructor(
    private val imageDataSource: ImageDataSource,
    private val authDataSource: AuthDataSource
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
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")

        return imageDataSource.getImage(eventId, imageId, token);
    }

    suspend fun addImage(eventId: String, imageByte: ByteArray) {
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")

        return imageDataSource.addImage(eventId, imageByte, token)
    }

    suspend fun deleteImage(eventId: String, imageId: String) {
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")

        imageDataSource.deleteImage(eventId, imageId, token)
    }
}
