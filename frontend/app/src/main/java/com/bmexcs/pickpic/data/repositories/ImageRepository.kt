package com.bmexcs.pickpic.data.repositories

import android.graphics.Bitmap
import com.bmexcs.pickpic.data.sources.ImageDataSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImageRepository @Inject constructor(
    private val imageDataSource: ImageDataSource
) {

    suspend fun getImageByImageId(imageId: String): String {
        return imageDataSource.getImageBinary(imageId);
    }

    suspend fun addImageBinary(imageByte: ByteArray?) : String {
        return imageDataSource.addImageBinary(imageByte)
    }
}
