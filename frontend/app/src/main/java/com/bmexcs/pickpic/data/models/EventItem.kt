package com.bmexcs.pickpic.data.models

import com.bmexcs.pickpic.data.serializable.SerializableUUID
import kotlinx.serialization.Serializable

// Response type for list-user-events
@Serializable
data class ListUserEventsResponse(
    val owned_events: List<ListUserEventItem>,
    val invited_events: List<ListUserEventItem>
)

// Data type for response from list-user-events
@Serializable
data class ListUserEventItem(
    val event_id: SerializableUUID,
    val name: String,
    val owner: String,
    val pfp_path: String,
)

@Serializable
data class EventItem(
    val id: Int,
    val event: EventInfo,
    val user: User
)

@Serializable
data class EventInfo(
    val event: String,
    val owner_id: Owner
)

@Serializable
data class Owner(
    val user_id: String,
    val display_name: String,
    val email: String,
    val phone: String,
    val profile_picture: String
)

@Serializable
data class User(
    val user_id: String,
    val display_name: String,
    val email: String,
    val phone: String,
    val profile_picture: String
)