package com.bmexcs.pickpic.data.models

import kotlinx.serialization.Serializable

// Response type for list-user-events
@Serializable
data class EventListResponse(
    val ownedEvents: List<EventListItem>,
    val invitedEvents: List<EventListItem>
)
