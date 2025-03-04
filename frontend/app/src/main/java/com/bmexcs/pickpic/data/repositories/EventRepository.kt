package com.bmexcs.pickpic.data.repositories

import com.bmexcs.pickpic.data.models.CreateEvent
import com.bmexcs.pickpic.data.models.ListUserEventsItem
import com.bmexcs.pickpic.data.serializable.SerializableUUID
import com.bmexcs.pickpic.data.sources.EventDataSource
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventRepository @Inject constructor(
    private val eventDataSource: EventDataSource
) {
    suspend fun getEvents(userId: String): List<ListUserEventsItem> {
        return eventDataSource.getEvents(userId)
    }

    suspend fun createEvent(name: String): CreateEvent {
        val newEvent = CreateEvent(
            event_name = name,
            owner = SerializableUUID(UUID.fromString("68e24b1a-36c8-4de5-a751-ba414e77db0b"))
        )
        eventDataSource.postEvent(newEvent)
        return newEvent
    }

    fun addUserToEvent(eventId: String) {
        // TODO: add user to event
    }
}
