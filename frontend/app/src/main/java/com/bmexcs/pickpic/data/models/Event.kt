package com.bmexcs.pickpic.data.models

import com.bmexcs.pickpic.data.serializable.SerializableUUID
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Event(
    val id: SerializableUUID = SerializableUUID(UUID(0, 0)),
    val ownerId: SerializableUUID = SerializableUUID(UUID(0, 0)),
    val userIds: List<SerializableUUID> = listOf(),
    val imageIds: List<SerializableUUID> = listOf()
)
