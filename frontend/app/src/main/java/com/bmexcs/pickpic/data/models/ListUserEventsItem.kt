package com.bmexcs.pickpic.data.models

import com.bmexcs.pickpic.data.serializable.SerializableUUID
import kotlinx.serialization.Serializable
import java.util.UUID

// Data type for response from list-user-events
@Serializable
data class ListUserEventsItem(
    val event_id: SerializableUUID = SerializableUUID(UUID(0, 0)),
    val name: String = "",
    val owner: String = "",
    val pfp_path: String = ""
)
