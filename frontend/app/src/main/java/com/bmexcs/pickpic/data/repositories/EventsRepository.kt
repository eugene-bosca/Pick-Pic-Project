package com.bmexcs.pickpic.data.repositories

import android.util.Log
import com.bmexcs.pickpic.data.models.Event
import com.bmexcs.pickpic.data.models.Image
import com.bmexcs.pickpic.data.sources.EventsDataSource
import okhttp3.OkHttp
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventsRepository @Inject constructor(
    private val authRepository: AuthRepository,
    private val eventsDataSource: EventsDataSource
) {

    fun getEvents(): List<Event> {
        // TODO: access the user id, etc.
        return listOf()
    }

    suspend fun getImageByEventId(eventId: Int): List<String> {
        return eventsDataSource.getImageByEventId(eventId)
    }

    suspend fun addImageByEventId(image: Image) {
        eventsDataSource.addImageByEventId(image)
    }

    suspend fun deleteImageByEventId(image: Image) {
        eventsDataSource.deleteImageByEventId(image)
    }
}
