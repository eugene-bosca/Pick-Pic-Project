package com.bmexcs.pickpic.data.repositories

import com.bmexcs.pickpic.data.sources.ImageDataSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImageRepository @Inject constructor(
    private val imageDataSource: ImageDataSource
) {
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
