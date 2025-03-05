package com.bmexcs.pickpic.data.sources

import android.util.Log
import com.bmexcs.pickpic.data.models.EventContent
import com.bmexcs.pickpic.data.models.EventPicture
import com.bmexcs.pickpic.data.utils.ApiService
import com.bmexcs.pickpic.data.utils.HttpContentType
import com.bmexcs.pickpic.data.utils.NotFoundException
import javax.inject.Inject


class EventsDataSource @Inject constructor(
    private val authDataSource: AuthDataSource
) {
    suspend fun getImagesByEventId(eventId: String): List<EventPicture> {
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")

        val eventContentList = try {
            val response = ApiService.get("event_contents/$eventId/", Array<EventPicture>::class.java, token)
            response.toMutableList()
        } catch (e: NotFoundException) {
            emptyList()
        }

        return eventContentList
    }

    suspend fun addImageByEvent(eventContent: EventContent) {
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")
        ApiService.post("event-contents/", eventContent, String::class.java, token)
    }

    suspend fun deleteImageByEventId(imageId: String) {
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")
        ApiService.delete("event-contents", EventContent::class.java, token)
    }
}
