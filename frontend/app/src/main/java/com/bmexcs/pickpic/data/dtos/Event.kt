package com.bmexcs.pickpic.data.dtos

import kotlinx.serialization.Serializable

@Serializable
data class Event(
    val event_id: String = "",
    val owner: Owner,
    val event_name: String = "",
    val last_modified: String = ""
)
