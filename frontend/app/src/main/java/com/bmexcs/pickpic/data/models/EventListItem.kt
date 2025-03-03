package com.bmexcs.pickpic.data.models

import com.bmexcs.pickpic.data.serializable.SerializableUUID
import kotlinx.serialization.Serializable

// Data type for response from list-user-events
@Serializable
data class EventListItem(
    val eventId: SerializableUUID,
    val name: String,
    val owner: String,
    val profilePicturePath: String,
)
