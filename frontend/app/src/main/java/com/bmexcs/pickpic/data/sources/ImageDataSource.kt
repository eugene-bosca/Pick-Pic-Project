package com.bmexcs.pickpic.data.sources

import com.bmexcs.pickpic.data.utils.apiServices.PictureAPIService
import javax.inject.Inject

class ImageDataSource @Inject constructor(
    private val authDataSource: AuthDataSource
) {
    suspend fun getImageBinary(eventId:String, imageId: String): ByteArray? {
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")

        return PictureAPIService.downloadPicture(eventId, imageId, token)
    }

    suspend fun addImageBinary(eventId: String, imageByte: ByteArray) {
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")

        PictureAPIService.uploadPicture(eventId, imageByte, token)
    }
}

