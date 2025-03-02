package com.bmexcs.pickpic.data.repositories

import android.graphics.Bitmap
import android.util.Log
import androidx.core.graphics.createBitmap
import com.bmexcs.pickpic.data.models.Event
import com.bmexcs.pickpic.data.models.Image
import com.bmexcs.pickpic.data.sources.EventsDataSource
import com.bmexcs.pickpic.data.sources.ImageDataSource
import okhttp3.OkHttp
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImageRepository @Inject constructor(
    private val imageDataSource: ImageDataSource
) {

    suspend fun getImageByImageId(imageId: SerializableUUID): Bitmap? {
        return imageDataSource.getImageByImageId(imageId);
    }
}
