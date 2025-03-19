package com.bmexcs.pickpic.data.repositories

import com.bmexcs.pickpic.data.models.EventMetadata
import com.bmexcs.pickpic.data.models.ImageMetadata
import com.bmexcs.pickpic.data.models.UserMetadata
import com.bmexcs.pickpic.data.sources.EventDataSource
import com.bmexcs.pickpic.data.models.VoteKind
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventRepository @Inject constructor(
    private val eventDataSource: EventDataSource
) {
    private val _eventInfo = MutableStateFlow(EventMetadata())
    val event = _eventInfo

    private var timestamp: Long = 0

    suspend fun isUpdated(eventId: String): Boolean {
        val lastModified =  eventDataSource.getEventLastModified(eventId)

        return if (lastModified > timestamp) {
            timestamp = lastModified
            true
        } else {
            false
        }
    }

    fun setCurrentEvent(eventInfo: EventMetadata) {
        event.value = eventInfo
    }

    suspend fun getAllEventsMetadata(): List<EventMetadata> {
        return eventDataSource.getAllEventsMetadata()
    }

    suspend fun getEventMetadata(eventId: String): EventMetadata {
        return eventDataSource.getEventMetadata(eventId)
    }

    suspend fun getEventOwnerMetadata(ownerId: String): UserMetadata {
        return eventDataSource.getEventOwnerMetadata(ownerId)
    }

    suspend fun getEventUsersMetadata(eventId: String): List<UserMetadata> {
        return eventDataSource.getEventUsersMetadata(eventId)
    }

    suspend fun getAllImagesMetadata(eventId: String): List<ImageMetadata> {
        return eventDataSource.getAllImagesMetadata(eventId)
    }

    suspend fun getUnrankedImagesMetadata(): List<ImageMetadata> {
        return eventDataSource.getUnrankedImagesMetadata(event.value.id)
    }

    suspend fun voteOnImage(imageId: String, voteKind: VoteKind) {
        eventDataSource.voteOnImage(event.value.id, imageId, voteKind)
    }

    suspend fun createEvent(name: String): EventMetadata {
        return eventDataSource.createEvent(name)
    }

    suspend fun getPendingEventsMetadata(): List<EventMetadata> {
        return eventDataSource.getPendingEventsMetadata()
    }

    suspend fun deleteEvent(id: String) {
        eventDataSource.deleteEvent(id)
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

    suspend fun removeUserFromEvent(eventId: String, userId: String) {
        eventDataSource.removeUserFromEvent(eventId, userId)
    }

    suspend fun inviteUsersFromEmail(userIds: List<String>, eventId: String)  {
        return eventDataSource.inviteUsersFromEmail(userIds, eventId)
    }
}
