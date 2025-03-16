package com.bmexcs.pickpic.data.sources

import com.bmexcs.pickpic.data.models.ImageInfo
import com.bmexcs.pickpic.data.services.EventApiService
import javax.inject.Inject

class ImageDataSource @Inject constructor(
    private val authDataSource: AuthDataSource
) {

    private val eventApi = EventApiService()

    suspend fun getImageBinary(eventId:String, imageId: String): ByteArray? {
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")

        return eventApi.downloadImage(eventId, imageId, token)
    }

    suspend fun addImageBinary(eventId: String, imageByte: ByteArray) {
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")

        eventApi.uploadImage(eventId, imageByte, token)
    }

    suspend fun deleteImage(eventId: String, imageId: String) {
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")

        eventApi.deleteImage(eventId, imageId, token)
    }

    suspend fun upvoteImage(eventId: String, imageId: String) {
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")

        eventApi.upvote(eventId, imageId, token)
    }

    suspend fun downvoteImage(eventId: String, imageId: String) {
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")

        eventApi.downvote(eventId, imageId, token)
    }
}
