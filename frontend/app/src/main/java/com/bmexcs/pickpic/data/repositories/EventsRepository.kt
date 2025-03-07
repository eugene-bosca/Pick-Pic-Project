package com.bmexcs.pickpic.data.repositories

import com.bmexcs.pickpic.data.models.Event
import com.bmexcs.pickpic.data.models.EventPicture
import com.bmexcs.pickpic.data.sources.EventDataSource
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventsRepository @Inject constructor(
    private val eventDataSource: EventDataSource
) {

    private val _event = MutableStateFlow<Event>(Event())
    val event = _event

    suspend fun deleteImageByEventId(imageId: String) {
        eventDataSource.deleteImageByEventId(imageId)
    }

    suspend fun getImageByEventId(eventId: String): List<EventPicture> {
        return eventDataSource.getImagesByEventId(eventId)
    }
    suspend fun addUserToEvent(): Int {
        return 1;
    }
}
