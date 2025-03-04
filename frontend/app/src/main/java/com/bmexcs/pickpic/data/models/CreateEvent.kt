package com.bmexcs.pickpic.data.models

import com.bmexcs.pickpic.data.serializable.SerializableUUID
import kotlinx.serialization.Serializable

@Serializable
data class CreateEvent(
    val event_name: String,
    val owner: SerializableUUID
)
