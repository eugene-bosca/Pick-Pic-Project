package com.bmexcs.pickpic.data.repositories

import android.util.Log
import com.bmexcs.pickpic.data.models.EventInfo
import com.bmexcs.pickpic.data.models.Invitation
import com.bmexcs.pickpic.data.models.ImageInfo
import com.bmexcs.pickpic.data.models.User
import com.bmexcs.pickpic.data.sources.EventDataSource
import com.bmexcs.pickpic.data.utils.Vote
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "EventRepository"

@Singleton
class EventRepository @Inject constructor(
    private val eventDataSource: EventDataSource
) {
    private val _eventInfo = MutableStateFlow(EventInfo())
    val event = _eventInfo

    private var timestamp: Long = 0

    suspend fun getEvents(): List<EventInfo> {
        return eventDataSource.getEvents()
    }

    suspend fun getEventInfo(eventId: String): EventInfo {
        return eventDataSource.getEventInfo(eventId)
    }

    suspend fun getEventOwnerInfo(ownerId: String): User {
        return eventDataSource.getEventOwnerInfo(ownerId)
    }

    suspend fun getAllImageInfo(eventId: String): List<ImageInfo> {
        return eventDataSource.getAllImageInfo(eventId)
    }

    suspend fun getUnrankedImageInfo(): List<ImageInfo> {
        return eventDataSource.getUnrankedImageInfo(event.value.event_id)
    }

    suspend fun voteOnImage(imageId: String, vote: Vote) {
        Log.d(TAG, "voteOnImage")
        eventDataSource.voteOnImage(event.value.event_id, imageId, vote)
    }

    suspend fun isUpdated(eventId: String): Boolean {
        val lastModified =  eventDataSource.getEventLastModified(eventId)

        return if (lastModified > timestamp) {
            timestamp = lastModified
            true
        } else {
            false
        }
    }

    suspend fun createEvent(name: String): EventInfo {
        return eventDataSource.createEvent(name)
    }

    suspend fun deleteEvent(id: String) {
        eventDataSource.deleteEvent(id)
    }

    suspend fun getUserEventsPending(): List<Invitation> {
        return eventDataSource.getEventsPending()
    }

    suspend fun acceptEvent(eventId: String) {
        eventDataSource.acceptEvent(eventId)
    }

    suspend fun declineEvent(eventId: String) {
        eventDataSource.declineEvent(eventId)
    }

    suspend fun addUserToEvent(eventId: String, userId: String) {
        eventDataSource.addUserToEvent(eventId, userId)
    }

    fun setCurrentEvent(eventInfo: EventInfo) {
        event.value = eventInfo
    }

    suspend fun inviteUsersWithId(userIds: List<String>, eventId: String)  {
        return eventDataSource.inviteUsersFromEmail(userIds, eventId)
    }
}
