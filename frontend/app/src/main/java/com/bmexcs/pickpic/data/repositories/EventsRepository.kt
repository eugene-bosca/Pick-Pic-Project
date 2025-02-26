package com.bmexcs.pickpic.data.repositories

import com.bmexcs.pickpic.data.models.Event
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventsRepository @Inject constructor(
    private val authRepository: AuthRepository
) {

    fun getEvents(): List<Event> {
        // TODO: access the user id, etc.
        return listOf()
    }
}
