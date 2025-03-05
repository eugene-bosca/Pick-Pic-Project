package com.bmexcs.pickpic.data.repositories

import com.bmexcs.pickpic.data.models.Event
import com.bmexcs.pickpic.data.models.EventContent
import com.bmexcs.pickpic.data.models.EventPicture
import com.bmexcs.pickpic.data.sources.EventsDataSource
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventsRepository @Inject constructor(
    private val eventsDataSource: EventsDataSource
) {

    private val _event = MutableStateFlow<Event>(Event())
    val event = _event

    fun getEvents(): List<Event> {
        // TODO: access the user id, etc.
        return listOf()
    }
    
    fun addUserToEvent(eventId: String) {
        // TODO: add user to event
    }

    suspend fun getImageByEventId(eventId: String): List<EventPicture> {
        return eventsDataSource.getImagesByEventId(eventId)
    }

    suspend fun addImageByEvent(eventContent: EventContent) {
        eventsDataSource.addImageByEvent(eventContent)
    }

    suspend fun deleteImageByEventId(imageId: String) {
        eventsDataSource.deleteImageByEventId(imageId)
    }

    suspend fun addUserToEvent(): Int {
        return 1;
    }
}
