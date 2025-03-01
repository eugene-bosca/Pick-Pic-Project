package com.bmexcs.pickpic.data.models

import com.bmexcs.pickpic.data.serializable.SerializableUUID
import kotlinx.serialization.Serializable

@Serializable
data class Image(
    val id: SerializableUUID,
    val url: String,
    val eventId: SerializableUUID,
    val score: Int
)
