package com.bmexcs.pickpic.data.repositories

import com.bmexcs.pickpic.data.models.CreateEvent
import com.bmexcs.pickpic.data.models.ListUserEventsItem
import com.bmexcs.pickpic.data.sources.EventDataSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventRepository @Inject constructor(
    private val eventDataSource: EventDataSource,
) {
    suspend fun getEvents(): List<ListUserEventsItem> {
        return eventDataSource.getEvents()
    }

    suspend fun createEvent(name: String): CreateEvent {
        return eventDataSource.postEvent(name)
    }

    fun addUserToEvent(eventId: String) {
        // TODO: add user to event
    }
}
