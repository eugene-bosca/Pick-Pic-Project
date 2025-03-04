package com.bmexcs.pickpic.data.sources

import com.bmexcs.pickpic.data.models.Event
import com.bmexcs.pickpic.data.models.EventContent
import com.bmexcs.pickpic.data.utils.ApiService
import javax.inject.Inject


class EventsDataSource @Inject constructor(
    private val authDataSource: AuthDataSource
) {
    suspend fun getImagesByEventId(eventId: String): List<EventContent> {
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")
        val response = ApiService.fetch("event-contents/$eventId", Array<EventContent>::class.java, token)

        return response.toList()
    }

    suspend fun addImageByEventId(eventContent: EventContent) {
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")
        ApiService.post("event-contents", eventContent, EventContent::class.java, token)
    }

    suspend fun deleteImageByEventId(imageId: String) {
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")
        ApiService.delete("event-contents", EventContent::class.java, token)
    }
}
