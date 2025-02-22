package com.bmexcs.pickpic.data.events

import com.bmexcs.pickpic.data.auth.AuthRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventsRepository @Inject constructor(
    private val authRepository: AuthRepository
) {

    fun getEvents(): List<Event> {
        // TODO: access the user id, etc.
        val userId = "xd"
        return fetchEventsForUser(userId)
    }

    // TODO: move to data source (and actually invoke the db)
    private fun fetchEventsForUser(userId: String): List<Event> {
        return listOf(
            Event("Event 1", userIds = listOf("xd", "lol", "haha")),
            Event("Event 2", userIds = listOf("no xding allowed"))
        ).filter { it.userIds.contains(userId) }
    }
}
