package com.bmexcs.pickpic.data.sources

import com.bmexcs.pickpic.data.models.EventContent
import com.bmexcs.pickpic.data.models.EventPicture
import com.bmexcs.pickpic.data.utils.ApiService
import com.bmexcs.pickpic.data.utils.NotFoundException
import com.bmexcs.pickpic.data.utils.apiServices.deleteImage
import com.bmexcs.pickpic.data.utils.apiServices.getEventContents
import com.bmexcs.pickpic.data.utils.apiServices.postImageByEvent
import javax.inject.Inject


class EventsDataSource @Inject constructor(
    private val authDataSource: AuthDataSource
) {
    suspend fun getImagesByEventId(eventId: String): List<EventPicture> {
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")

        val eventContentList = try {
            val response = getEventContents(eventId, token)
            response.toMutableList()
        } catch (e: NotFoundException) {
            emptyList()
        }

        return eventContentList
    }

    suspend fun addImageByEvent(eventContent: EventContent) {
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")
//        ApiService.post("event-contents/", eventContent, String::class.java, token)
        postImageByEvent(eventContent.image.image_id, token)
    }

    // TODO im sure this request doesn't actually work
    // not sure how this actually works.
    suspend fun deleteImageByEventId(imageId: String) {
        val token = authDataSource.getIdToken() ?: throw Exception("No user token")
        // uses Eventcontent class?
        deleteImage(imageId, token)
    }
}
