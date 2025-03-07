package com.bmexcs.pickpic.data.repositories

import com.bmexcs.pickpic.data.models.CreateEvent
import com.bmexcs.pickpic.data.models.Event
import com.bmexcs.pickpic.data.models.EventPicture
import com.bmexcs.pickpic.data.models.ListUserEventsItem
import com.bmexcs.pickpic.data.sources.EventDataSource
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventRepository @Inject constructor(
    private val eventDataSource: EventDataSource,
) {

    private val _event = MutableStateFlow<Event>(Event())
    val event = _event

    suspend fun deleteImageByEventId(imageId: String) {
        eventDataSource.deleteImageByEventId(imageId)
    }

    suspend fun getImageByEventId(eventId: String): List<EventPicture> {
        return eventDataSource.getImagesByEventId(eventId)
    }

    suspend fun getEvents(): List<ListUserEventsItem> {
        return eventDataSource.getEvents()
    }

    suspend fun createEvent(name: String): CreateEvent {
        return eventDataSource.postEvent(name)
    }

    fun addUserToEvent(eventId: String) {
        // TODO: add user to event
    }

    suspend fun getUserEventsPending(): List<ListUserEventsItem> {
        return eventDataSource.getUserEventsPending()
    }

    suspend fun acceptEvent(eventId: String) {
        eventDataSource.acceptEvent(eventId)
    }

    suspend fun declineEvent(eventId: String) {
        eventDataSource.declineEvent(eventId)
    }

}
