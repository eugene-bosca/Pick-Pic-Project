package com.bmexcs.pickpic.data.models

import kotlinx.serialization.Serializable

// Response type for list-user-events
@Serializable
data class ListUserEventsResponse(
    val owned_events: List<ListUserEventsItem> = listOf(),
    val invited_events: List<ListUserEventsItem> = listOf()
)
