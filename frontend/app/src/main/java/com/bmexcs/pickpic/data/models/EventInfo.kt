package com.bmexcs.pickpic.data.models

import kotlinx.serialization.Serializable

@Serializable
data class EventInfo(
    val event_id: String = "",
    val event_name: String = "",
    val last_modified: String = "",
    val owner: String = ""
)
