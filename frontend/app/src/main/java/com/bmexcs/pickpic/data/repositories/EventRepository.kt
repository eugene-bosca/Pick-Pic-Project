package com.bmexcs.pickpic.data.repositories

import com.bmexcs.pickpic.data.models.EventInfo
import com.bmexcs.pickpic.data.models.EventMember
import com.bmexcs.pickpic.data.models.ImageInfo
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

    suspend fun getEvents(): List<EventInfo> {
        return eventDataSource.getEvents()
    }

    suspend fun createEvent(name: String): EventInfo {
        return eventDataSource.createEvent(name)
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

    suspend fun getImages(eventId: String): List<ImageInfo> {
        return eventDataSource.getImageInfo(eventId)
    }
}
