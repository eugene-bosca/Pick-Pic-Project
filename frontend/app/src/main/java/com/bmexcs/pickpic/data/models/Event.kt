package com.bmexcs.pickpic.data.models

import kotlinx.serialization.Serializable

@Serializable
data class Event(
    val event_id: String = "",
    val event_name: String = "",
)
