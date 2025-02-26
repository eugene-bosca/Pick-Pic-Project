package com.bmexcs.pickpic.data.models

import com.bmexcs.pickpic.data.serializable.SerializableUUID
import kotlinx.serialization.Serializable

@Serializable
data class Event(
    val id: SerializableUUID,
    val ownerId: SerializableUUID,
    val userIds: List<SerializableUUID> = listOf(),
    val imageIds: List<SerializableUUID> = listOf()
    // TODO: rankings?
)
