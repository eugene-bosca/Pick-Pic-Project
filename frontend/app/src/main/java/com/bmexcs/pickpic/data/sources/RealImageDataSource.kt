package com.bmexcs.pickpic.data.sources

import com.bmexcs.pickpic.data.services.EventApiService

class RealImageDataSource : ImageDataSource {
    private val eventApi = EventApiService()

    override suspend fun getImage(eventId: String, imageId: String, token: String): ByteArray? {
        return try {
            eventApi.downloadImage(eventId, imageId, token)
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun addImage(eventId: String, imageData: ByteArray, token: String) {
        eventApi.uploadImage(eventId, imageData, token)
    }

    override suspend fun deleteImage(eventId: String, imageId: String, token: String) {
        eventApi.deleteImage(eventId, imageId, token)
    }

    override fun clearCache() {}
}