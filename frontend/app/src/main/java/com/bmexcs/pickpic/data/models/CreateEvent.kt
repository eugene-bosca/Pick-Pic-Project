package com.bmexcs.pickpic.data.models

import com.bmexcs.pickpic.data.serializable.SerializableUUID
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class CreateEvent(
    val event_name: String,
    val owner: SerializableUUID = SerializableUUID(UUID(0, 0))
)
