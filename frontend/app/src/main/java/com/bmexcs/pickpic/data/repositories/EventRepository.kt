package com.bmexcs.pickpic.data.repositories

import com.bmexcs.pickpic.data.models.Event
import com.bmexcs.pickpic.data.models.EventCreation
import com.bmexcs.pickpic.data.models.EventUser
import com.bmexcs.pickpic.data.sources.EventDataSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventRepository @Inject constructor(
    private val eventDataSource: EventDataSource,
) {
    suspend fun getEvents(): List<Event> {
        return eventDataSource.getEvents()
    }

    suspend fun createEvent(name: String): Event {
        return eventDataSource.createEvent(name)
    }

    fun addUserToEvent(eventId: String) {
        // TODO: add user to event
    }

    suspend fun getUserEventsPending(): List<EventUser> {
        return eventDataSource.getEventsPending()
    }

    suspend fun acceptEvent(eventId: String) {
        eventDataSource.acceptEvent(eventId)
    }

    suspend fun declineEvent(eventId: String) {
        eventDataSource.declineEvent(eventId)
    }
}
