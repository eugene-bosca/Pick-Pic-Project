package com.bmexcs.pickpic.data.repositories

import com.bmexcs.pickpic.data.models.EventInfo
import com.bmexcs.pickpic.data.models.EventOwner
import com.bmexcs.pickpic.data.models.EventMember
import com.bmexcs.pickpic.data.models.EventPicture
import com.bmexcs.pickpic.data.sources.EventDataSource
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventRepository @Inject constructor(
    private val eventDataSource: EventDataSource,
) {
    private val _eventInfo = MutableStateFlow(EventInfo())
    val event = _eventInfo

    suspend fun getEvents(): List<EventOwner> {
        return eventDataSource.getEvents()
    }

    suspend fun createEvent(name: String): EventInfo {
        return eventDataSource.createEvent(name)
    }

    fun addUserToEvent(eventId: String) {
        // TODO: add user to event
    }

    suspend fun getUserEventsPending(): List<EventMember> {
        return eventDataSource.getEventsPending()
    }

    suspend fun acceptEvent(eventId: String) {
        eventDataSource.acceptEvent(eventId)
    }

    suspend fun declineEvent(eventId: String) {
        eventDataSource.declineEvent(eventId)
    }

    suspend fun deleteImageByEventId(imageId: String) {
        eventDataSource.deleteImageByEventId(imageId)
    }

    suspend fun getImageByEventId(eventId: String): List<EventPicture> {
        return eventDataSource.getImagesByEventId(eventId)
    }
}
