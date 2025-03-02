package com.bmexcs.pickpic.data.repositories

import android.util.Log
import com.bmexcs.pickpic.data.models.Event
import com.bmexcs.pickpic.data.models.Image
import com.bmexcs.pickpic.data.sources.EventsDataSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventsRepository @Inject constructor(
    private val eventsDataSource: EventsDataSource
) {

    fun getEvents(): List<Event> {
        // TODO: access the user id, etc.
        return listOf()
    }
    
    fun addUserToEvent(eventId: String) {
        // TODO: add user to event
    }

    suspend fun getImageByEventId(eventId: String): List<Image> {
        return eventsDataSource.getImagesByEventId(eventId)
    }

    suspend fun addImageByEventId(image: Image) {
        eventsDataSource.addImageByEventId(image)
    }

    suspend fun deleteImageByEventId(imageId: String) {
        eventsDataSource.deleteImageByEventId(imageId)
    }

    suspend fun addUserToEvent(): Int {
        return 1;
    }
}
