package com.bmexcs.pickpic.data.sources

import android.util.Log
import com.bmexcs.pickpic.data.services.EventApiService

private const val TAG = "ImageDataSource"

class ImageDataSource {
    private val eventApi = EventApiService()

    suspend fun getImage(eventId: String, imageId: String, token: String): ByteArray? {
        Log.d(TAG, "getImage for event $eventId and image $imageId")

        return eventApi.downloadImage(eventId, imageId, token)
    }

    suspend fun addImage(eventId: String, imageData: ByteArray, token: String) {
        Log.d(TAG, "addImage for event $eventId")

        eventApi.uploadImage(eventId, imageData, token)
    }

    suspend fun deleteImage(eventId: String, imageId: String, token: String) {
        Log.d(TAG, "deleteImage for event $eventId and image $imageId")

        eventApi.deleteImage(eventId, imageId, token)
    }
}
