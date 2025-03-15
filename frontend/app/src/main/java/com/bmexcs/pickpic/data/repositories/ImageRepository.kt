package com.bmexcs.pickpic.data.repositories

import com.bmexcs.pickpic.data.sources.ImageDataSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImageRepository @Inject constructor(
    private val imageDataSource: ImageDataSource
) {
    suspend fun getImageByImageId(eventId: String, imageId: String): ByteArray? {
        return imageDataSource.getImageBinary(eventId, imageId);
    }

    suspend fun addImageBinary(eventId:String, imageByte: ByteArray) {
        return imageDataSource.addImageBinary(eventId, imageByte)
    }

    suspend fun deleteImage(eventId: String, imageId: String) {
        imageDataSource.deleteImage(eventId, imageId)
    }
}
