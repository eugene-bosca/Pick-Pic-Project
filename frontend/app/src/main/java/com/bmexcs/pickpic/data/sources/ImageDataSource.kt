package com.bmexcs.pickpic.data.sources

import android.util.Log
import com.bmexcs.pickpic.data.services.EventApiService
import javax.inject.Inject

private const val TAG = "ImageDataSource"

class ImageDataSource @Inject constructor(
    private val authDataSource: AuthDataSource
) {
    private val eventApi = EventApiService()

    suspend fun getImage(eventId: String, imageId: String): ByteArray? {
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")

        Log.d(TAG, "getImage for event $eventId and image $imageId")

        return eventApi.downloadImage(eventId, imageId, token)
    }

    suspend fun addImage(eventId: String, imageData: ByteArray) {
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")

        Log.d(TAG, "addImage for event $eventId")

        eventApi.uploadImage(eventId, imageData, token)
    }

    suspend fun deleteImage(eventId: String, imageId: String) {
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")

        Log.d(TAG, "deleteImage for event $eventId and image $imageId")

        eventApi.deleteImage(eventId, imageId, token)
    }
}
