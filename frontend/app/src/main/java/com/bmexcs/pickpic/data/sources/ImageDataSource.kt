package com.bmexcs.pickpic.data.sources

import com.bmexcs.pickpic.data.services.EventApiService
import javax.inject.Inject

class ImageDataSource @Inject constructor(
    private val authDataSource: AuthDataSource,
    private val userDataSource: UserDataSource
) {

    private val eventApi = EventApiService()

    suspend fun getImageBinary(eventId:String, imageId: String): ByteArray? {
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")

        return eventApi.downloadImage(eventId, imageId, token)
    }

    suspend fun addImageBinary(eventId: String, imageByte: ByteArray) {
        val userId = userDataSource.getUser().user_id
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")

        eventApi.uploadImage(eventId, userId, imageByte, token)
    }

    suspend fun deleteImage(eventId: String, imageId: String) {
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")

        eventApi.deleteImage(eventId, imageId, token)
    }
}
