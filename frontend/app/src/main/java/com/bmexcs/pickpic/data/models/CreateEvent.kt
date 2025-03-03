package com.bmexcs.pickpic.data.models

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class CreateEvent(
    val event_name: String,
    @Contextual val owner: UUID
)
